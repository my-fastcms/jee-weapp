var WxParse = require('../../wxParse/wxParse.js');
var app = getApp();
Page({
  data: {
    detail: {},
    cartCount: 0,
    reviews: [],
    reviewInitFlag: false,
    page: 1,
    endFlag: false,
    loadingFlag: false,
    specificationArray: [],
    //tabs: ["商品详情", "商品评价"],
    activeIndex: 0,
    num: 1,
    minusStatuses: '',
    flag: 0,
    totalPrice: 0.00,
    attrValueList: [],
    groupings: null,
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor'),
    other_bgcolor: wx.getStorageSync('other_bgcolor'),
    show_safeguard: wx.getStorageSync('show_safeguard'),
    show_delivery_time: wx.getStorageSync('show_delivery_time'),
    show_compensate: wx.getStorageSync('show_compensate'),
    productId: null
  },

  onLoad: function (param) {
    this.data.productId = param.id;
    var that = this;
    wx.getSystemInfo({
      success: function (scr) {
        var scollHeight = scr.screenHeight - 90;
        app.ajaxJson('/bargain/detail', { id: param.id }, function (res) {
          var json = res.data;
         
          if (json.code != 200) {
            wx.showModal({
              showCancel: false,
              title: '错误',
              content: json.msg ? json.msg : "500",
            })
            return;
          }

          var detail = json.data.productDetail;
          that.setData({
            detail: detail,
            price: detail.product.price,
            totalPrice: 0.00,
            specificationArray: [],
            specPriceMap: detail.priceMap,
            groupings: detail.groupings || null,
            unTimeMillis: detail.unTimeMillis || null,
            cartCount: json.data.cartCount || 0,
            scollHeight: scollHeight,
            res: json.data.res,
            promotionInfo: detail.promotionInfo || null
          });

          WxParse.wxParse('article', 'html', detail.product.introduction, that, 0);
          that.distachAttrValue(that.data.detail); //init attr

          var unTimeMillis = that.data.unTimeMillis;
          if (unTimeMillis ) {  
            that.setTimeData(unTimeMillis);
          }

          //默认单规格情况下
          if (!detail.specifications || detail.specifications.length == 0) {
            var price = parseFloat(that.data.price);
            price = price.toFixed(2);
            var totalPrice = price * that.data.num;
            totalPrice = totalPrice.toFixed(2);
            that.setData({
              price: price,
              totalPrice: totalPrice
            });
          }
        });
      }
    })
    //that.getCartCount();
  },

  // getCartCount: function () {
  //   var that = this;
  //   app.ajaxJson('/cart/getCartCount', {}, function (res) {
  //     var json = res.data;
  //     if (json.code == 200) {
  //       var cartCount = json.data;
  //       that.setData({
  //         cartCount: cartCount
  //       });
  //     }
  //   });
  // },

  tabClick: function (e) {
    this.setData({
      activeIndex: e.currentTarget.id
    });
    if (e.currentTarget.id == 1 && !this.data.reviewInitFlag) {
      this.data.reviewInitFlag = true;
      this.getReviews(this.data.page);
    }
  },

  swiperchange: function () {

  },

  setTimeData: function (data) {
    var that = this;
    var time = data;
    if (time != null){
      setInterval(function () {
        var t = --time;
        var h = Math.floor(t / 60 / 60);
        var m = Math.floor((t - h * 60 * 60) / 60);
        var s = t % 60;
        if (h < 10) h = "0" + h;
        if (m < 10) m = "0" + m;
        if (s < 10) s = "0" + s;
        var curtime = '活动结束时间: '+h + '时' + m + '分' + s + '秒';
        that.setData({
          t:t || null,
          unTime: curtime
        })
      }, 1000)
    }
  },

  bindViewTap: function (e) {
    var param = e.currentTarget.dataset;
    var id = param["id"];
    var index = param["index"];
    this.setData({
      selected: id,
      firstIndex: index
    });
  },

  // onCartClick: function () {
  //   wx.reLaunch({
  //     url: '/pages/cart/cart'
  //   })
  // },

  setModalStatus: function (e) {
    var that = this;
    //清除已选择规格
    var attrValues = this.data.attrValueList;
    for (var k = 0; k < attrValues.length; k++) {
      if (attrValues[k].selectedValue && attrValues[k].selectedValue != null) {
        attrValues[k].selectedValue = null;
      }
    }

    this.setData({
      attrValueList: attrValues
    });

    var status = e.currentTarget.dataset.status;
    var animation = wx.createAnimation({
      duration: 200,
      timingFunction: "linear",
      delay: 0
    })
    this.animation = animation
    animation.translateY(300).step()
    this.setData({
      animationData: animation.export()
    })
    if (status == 1) {
      this.setData({
        showModalStatus: true
      });
    }
    var flag = e.currentTarget.dataset.flag;
    this.setData({
      flag: flag
    });

    var price = 0.00;
    if (flag && flag == 2) {
      //拼团情况显示拼团价格（单规格的情况，多规格另外处理）
      if (that.data.detail.specifications == null || that.data.detail.specifications.length <= 0) {
        price = parseFloat(that.data.detail.groupInfo.collagePrice);
      }
    } else {
      //单独购买或加入购物车
      if (that.data.detail.specifications == null || that.data.detail.specifications.length <= 0) {
        price = parseFloat(that.data.detail.product.price);
      }
    }
    if (that.data.promotionInfo != null) {
      var proprice = that.data.promotionInfo.promotionPrice;

      price = parseFloat(proprice);
    }
    price = price.toFixed(2);
    var totalPrice = price * that.data.num;
    totalPrice = totalPrice.toFixed(2);
    that.setData({
      price: price,
      totalPrice: totalPrice
    });

    setTimeout(function () {
      animation.translateY(0).step();
      this.setData({
        animationData: animation
      })
      if (status == 0) {
        this.setData({
          showModalStatus: false,
          num: 1
        });
      }
    }.bind(this), 100)
  },

  pay: function (e) {
    var that = this;
    var sflag = true;
    for (var i = 0; i < that.data.specificationArray.length; i++) {
      if (that.data.specificationArray[i].sfId == '') {
        sflag = false;
        break;
      }
    }
    if (!sflag) {
      wx.showToast({
        title: '请选择规格...',
        icon: 'success',
        duration: 1000
      })
      return;
    }
    var quantity = that.data.num;
    var speci = JSON.stringify(that.data.specificationArray);
    var productId = that.data.detail.product.id;
    var flag = that.data.flag;
 
    if (flag == 0) {
      //that._add2cart(that, productId, quantity, speci);
      //选中的规格id
      var specids = that.data.specificationArray;
      var specMap = that.data.specPriceMap;
    
      var barSpec = that.data.detail.bargainSpecifications;
      var barProSetId;
      if(specids.length == 0){
        barProSetId = barSpec[0].id;
       
      }else{
        var specId = "";
        for (var p = 0; p < specids.length; p++) {
          var spvId = specids[p].spvId;
          specId += spvId;
          if (p < specids.length-1){
            specId += ",";
          }
        }
    
        //var specDetail = this.data.specPriceMap[specId];
        //多规格
        //var specificationValue = specDetail.specificationValue;
        //console.log(productSetId);
  
        for (var p = 0; p < barSpec.length; p++) {
          var specItemId = barSpec[p].specificationValue;
          //多规格字符串
          if (specItemId == specId) {
            barProSetId = barSpec[p].id;
          }
        }
      }
      that.toBargainDetail(barProSetId);
    } else if (flag == 1 || flag == 2) {
      var itemsArray = new Array();
      var entity = new Object();
      entity.productId = productId;
      entity.pcount = quantity;
      entity.speci = speci;
      itemsArray.push(entity);
     
      //console.log(JSON.stringify(itemsArray));
      wx.navigateTo({
        url: '../pay/jiesuan?flag=' + flag + '&items=' + JSON.stringify(itemsArray)
      })
    }
    this.setData({
      showModalStatus: false
    });
  },
  //跳转到砍价页面, 发起砍价,也是第一次帮砍
  toBargainDetail: function(barProSetId){
    var that = this;
    wx.navigateTo({
      url: 'bargainProductDetail?barProSetId=' + barProSetId + '&formId=' + that.data.formId
    })
  },

  toBuyProduct: function (e) {
    var that = this;
    wx.redirectTo({
      url: '../product/detail/detail?id=' + that.data.productId,
    })
  },
  formSubmit: function (e) {
    var that = this;
    var formId = e.detail.formId || null;//用于模板消息推送
    //params.formIds = JSON.stringify(this.data.formIds)
   // console.log("===============formId:" + formId);
    this.setData({
      formId: formId
    });
    //console.log(that.data.formId);
  },

  // _add2cart: function (that, productId, quantity, speci) {
  //   var req = {};
  //   req.productId = productId;
  //   req.quantity = quantity;
  //   req.speci = speci;
  //   app.ajaxJson('/cart/addCart', req, function (res) {
  //     var json = res.data;
  //     if (json.code != 200) {
  //       wx.showModal({
  //         showCancel: false,
  //         title: '错误',
  //         content: json.msg ? json.msg : "500",
  //       })
  //     } else {
  //       var cartCount = json.data;
  //       wx.showToast({
  //         title: '加入购物车成功',
  //         icon: 'success',
  //         duration: 1000
  //       });
  //       that.setData({
  //         cartCount: cartCount
  //       });
  //     }
  //   });
  // },


  bindManual: function (e) {
    var num = parseInt(e.detail.value);
    if (isNaN(num)) {
      num = 1;
    }
    // 将数值与状态写回
    if (num > 0 && num < 100000) {
      this.setTotalPrice(num);
    }
  },

  setTotalPrice: function (num) {
    var totalPrice = '';
    if (!isNaN(this.data.price)) {
      totalPrice = this.data.price * num;
      totalPrice = totalPrice.toFixed(2);
    }
    this.setData({
      num: num,
      totalPrice: totalPrice
    });
  },

  bindManualTapped: function () {
    // 什么都不做，只为打断跳转
  },

  distachAttrValue: function (detail) {
    var attrValueList = this.data.attrValueList;
    var specificationArray = this.data.specificationArray;
    if (detail.specifications && detail.specifications.length > 0) {
      for (var i = 0; i < detail.specifications.length; i++) {
        var attrKey = "";
        var attrKeyId = "";
        var attrValues = [];
        var attrValuesId = [];
        var attrValueStatus = [];
        attrKey = detail.specifications[i].specification.name;
        attrKeyId = detail.specifications[i].specification.id;
        specificationArray.push({
          sfId: '',
          spvId: ''
        });
        for (var j = 0; j < detail.specifications[i].specificationValues.length; j++) {
          attrValues.push(detail.specifications[i].specificationValues[j].name);
          attrValuesId.push(detail.specifications[i].specificationValues[j].id);
          attrValueStatus.push(true);
        }
        attrValueList.push({
          attrKey: attrKey,
          attrKeyId: attrKeyId,
          attrValues: attrValues,
          attrValuesId: attrValuesId,
          attrValueStatus: attrValueStatus
        });
      }
    }

    this.setData({
      attrValueList: attrValueList
    });
  },

  selectAttrValue: function (e) {
    var me = this;
    var attrValueList = this.data.attrValueList;
    var specificationArray = this.data.specificationArray;
    var index = e.currentTarget.dataset.index;//属性索引
    var key = e.currentTarget.dataset.key;
    var value = e.currentTarget.dataset.value;
    var sfId = e.currentTarget.dataset.sfid;
    var spvId = e.currentTarget.dataset.id;
    if (e.currentTarget.dataset.status) {
      specificationArray[index].sfId = sfId;
      specificationArray[index].spvId = spvId;
      var spcs = [];
      for (var p = 0; p < specificationArray.length; p++) {
        spcs.push(specificationArray[p].spvId);
      }
      var spckey = spcs.join(",");
      var spec = this.data.specPriceMap[spckey];
      var price = 0;
      var totalPrice = '';
      if (spec) {
        price = spec.price;
        if (spec.promPrice != null && spec.promPrice != "") {
          price = spec.promPrice;
        }
        if (me.data.flag == 2 && spec.collagePrice != null && spec.collagePrice != "") {//拼团
          price = spec.collagePrice;
        }
        totalPrice = price * this.data.num;
        totalPrice = totalPrice.toFixed(2);
      }
      attrValueList[index].selectedValue = value;
      this.setData({
        price: price,
        totalPrice: totalPrice,
        attrValueList: attrValueList
      });
    }
  },

  //滑动底部加载
  lower: function () {
    // console.log('滑动底部加载', new Date());
    if (!this.data.endFlag && !this.data.loadingFlag) {
      var page = this.data.page + 1;
      this.getReviews(page);
    } else {
      wx.showToast({
        title: '已到最后'
      })
    }
  },

  getReviews: function (page) {
    if (this.data.endFlag) {
      return;
    }
    var that = this;
    that.data.page = page;
    var req = {}
    req.page = page;
    req.productId = that.data.productId;
    that.data.loadingFlag = true;
    app.ajaxJson('/product/getReviewsByPage', req, function (res) {
      that.data.loadingFlag = false;
      var json = res.data;
      if (json.code != 200) {
        that.data.endFlag = true;
        return;
      }
      if (json.data && json.data.list && json.data.list.length > 0) {
        that.setData({
          reviews: that.data.reviews.concat(json.data.list)
        });
      } else {
        that.data.endFlag = true;
      }
      if (json.data.lastPage) {
        that.data.endFlag = true;
      }
    }, function () {
      that.data.loadingFlag = false;
    });
  },

  previewImg: function () {
    wx.previewImage({
      //current: '', // 当前显示图片的http链接
      urls: this.data.detail.imageList // 需要预览的图片http链接列表
    })
  },

  onShareAppMessage: function (res) {
    var me = this;
    if (res.from === 'button') {
      // 来自页面内转发按钮
      //console.log(res.target)
    }
    return {
      title: "发现好货:" + me.data.detail.product.name,
      path: '/pages/product/detail/detail?id=' + me.data.detail.product.id,
      success: function (res) {
        // 转发成功
      },
      fail: function (res) {
        // 转发失败
      }
    }
  },

  goHome: function () {
    wx.switchTab({
      url: '/pages/index/index',
    })
  },

  collect: function () {
    wx.showToast({
      title: '已收藏',
    })
  },

  // joinGroup: function (e) {
  //   var id = e.currentTarget.dataset.groupid;
  //   wx.navigateTo({
  //     url: '/pages/group/detail?groupId=' + id,
  //   })
  // },
  onPullDownRefresh: function () {
    wx.stopPullDownRefresh();
  }
})