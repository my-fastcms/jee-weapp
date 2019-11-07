var WxParse = require('../../../wxParse/wxParse.js');
var app = getApp();
Page({
  data: {
    day: 0,
    hours: "00",
    minutes: "00",
    second: "00",
    detail: {},
    reviews: [],
    reviewInitFlag: false,
    page: 1,
    endFlag: false,
    loadingFlag: false,
    specificationArray: [],
    tabs: ["商品详情", "商品评价"],
    activeIndex: 0,
    num: 1,
    minusStatuses: '',
    flag: 0,
    timeFlag: 0,
    totalPrice: 0.00,
    attrValueList: [],
    startDate: '',
    endDate: '',
    timer: null,
    yuyue: null,
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor'),
    other_bgcolor: wx.getStorageSync('other_bgcolor'),
    show_safeguard: wx.getStorageSync('show_safeguard'),
    show_delivery_time: wx.getStorageSync('show_delivery_time'),
    show_compensate: wx.getStorageSync('show_compensate')

  },

  onLoad: function(param) {
    this.timer = setInterval(this.clock, 500);
    this.data.productId = param.id;
    var that = this;
    wx.getSystemInfo({
      success: function(scr) {
        var scollHeight = scr.screenHeight - 90;
        app.ajaxJson('/seckill/detail', {
          id: param.id
        }, function(res) {
          console.log(res);
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
            scollHeight: scollHeight,
            res: json.data.res,
            startDate: detail.seckillInfo.startDate,
            endDate: detail.seckillInfo.endDate
          });



          WxParse.wxParse('article', 'html', detail.product.introduction, that, 0);
          that.distachAttrValue(that.data.detail); //init attr

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
  },

  swiperchange: function() {

  },

  setModalStatus: function(e) {
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
    if (flag && flag == 5) {
      //秒杀情况显示秒杀价格（单规格的情况，多规格另外处理）
      if (that.data.detail.specifications == null || that.data.detail.specifications.length <= 0) {
        price = parseFloat(that.data.detail.seckillInfo.seckillMoney);
      }
    } else {
      //单独购买
      if (that.data.detail.specifications == null || that.data.detail.specifications.length <= 0) {
        price = parseFloat(that.data.detail.product.price);
      }
    }

    price = price.toFixed(2);
    var totalPrice = price * that.data.num;
    totalPrice = totalPrice.toFixed(2);
    that.setData({
      price: price,
      totalPrice: totalPrice
    });

    setTimeout(function() {
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

  pay: function(e) {
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
    var seckillProductId = that.data.detail.seckillInfo.seckillProductId;

    if (flag == 1 || flag == 5) {
      var itemsArray = new Array();
      var entity = new Object();
      entity.productId = productId;
      entity.pcount = quantity;
      entity.speci = speci;
      entity.seckillProductId = seckillProductId;
      itemsArray.push(entity);
      //console.log(JSON.stringify(itemsArray));
      wx.navigateTo({
        url: '../../pay/jiesuan?flag=' + flag + '&items=' + JSON.stringify(itemsArray) + '&seckillProductId=' + seckillProductId
      })
    }
    this.setData({
      showModalStatus: false
    });
  },

  bindMinus: function(e) {
    var num = this.data.num;
    // 如果只有1件了，就不允许再减了
    if (num > 1) {
      num--;
      this.setTotalPrice(num);
    }
  },

  bindPlus: function(e) {
    var num = this.data.num;
    if (num >= 99999) {
      return;
    }
    // 自增
    num++;
    this.setTotalPrice(num);
  },

  bindManual: function(e) {
    var num = parseInt(e.detail.value);
    if (isNaN(num)) {
      num = 1;
    }
    // 将数值与状态写回
    if (num > 0 && num < 100000) {
      this.setTotalPrice(num);
    }
  },

  setTotalPrice: function(num) {
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

  bindManualTapped: function() {
    // 什么都不做，只为打断跳转
  },

  distachAttrValue: function(detail) {
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

  selectAttrValue: function(e) {
    var me = this;
    var attrValueList = this.data.attrValueList;
    var specificationArray = this.data.specificationArray;
    var index = e.currentTarget.dataset.index; //属性索引
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
        if (me.data.flag == 5 && spec.seckillMoney != null && spec.seckillMoney != "") { //秒杀
          price = spec.seckillMoney;
        }
        if (spec.agentPrice != null && spec.agentPrice != "") {
          price = spec.agentPrice;
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

  // 滑动底部加载
  lower: function() {
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

  getReviews: function(page) {
    if (this.data.endFlag) {
      return;
    }
    var that = this;
    that.data.page = page;
    var req = {}
    req.page = page;
    req.productId = that.data.productId;
    that.data.loadingFlag = true;
    app.ajaxJson('/product/getReviewsByPage', req, function(res) {
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
    }, function() {
      that.data.loadingFlag = false;
    });
  },

  previewImg: function() {
    wx.previewImage({
      //current: '', // 当前显示图片的http链接
      urls: this.data.detail.imageList // 需要预览的图片http链接列表
    })
  },

  onShareAppMessage: function(res) {
    var me = this;
    if (res.from === 'button') {
      // 来自页面内转发按钮
      console.log(res.target)
    }
    return {
      title: "发现好货:" + me.data.detail.product.name,
      path: '/pages/product/detail/detail?id=' + me.data.detail.product.id,
      success: function(res) {
        // 转发成功
      },
      fail: function(res) {
        // 转发失败
      }
    }
  },

  toBuyProduct: function (e) {
    var that = this;
    wx.redirectTo({
      url: '../../product/detail/detail?id=' + that.data.productId,
    })
  },
  
  goHome: function() {
    wx.switchTab({
      url: '/pages/index/index',
    })
  },

  collect: function() {
    wx.showToast({
      title: '已收藏',
    })
  },

  onPullDownRefresh: function() {
    wx.stopPullDownRefresh();
  },

  //秒杀倒计时
  clock() {
    var today = new Date(); //当前时间
    var h = today.getHours();
    var m = today.getMinutes();
    var s = today.getSeconds();
    var stopTime = new Date(this.data.startDate); //开始秒杀时间
    var stopH = stopTime.getHours();
    var stopM = stopTime.getMinutes();
    var stopS = stopTime.getSeconds();
    var shenyu = stopTime.getTime() - today.getTime(); //倒计时毫秒数
    if (stopTime.getTime() > today.getTime()) {
      this.setData({
        yuyue: false
      })
    } else {
      this.setData({
        yuyue: true
      })
    }
    if (shenyu <= 0) {
      clearInterval(this.timer);
      this.setData({
        hours: "00",
        minutes: "00",
        second: "00",
      });
      return;
    }
    var shengyuD = parseInt(shenyu / (60 * 60 * 24 * 1000));
    var D = parseInt(shenyu) - parseInt(shengyuD * 60 * 60 * 24 * 1000); //除去天的毫秒数
    var shengyuH = parseInt(D / (60 * 60 * 1000));
    var H = D - shengyuH * 60 * 60 * 1000; //除去天、小时的毫秒数
    var shengyuM = parseInt(H / (60 * 1000));
    var M = H - shengyuM * 60 * 1000; //除去天、小时、分的毫秒数
    var S = parseInt((shenyu - shengyuD * 60 * 60 * 24 * 1000 - shengyuH * 60 * 60 * 1000 - shengyuM * 60 * 1000) / 1000);

    shengyuH = shengyuD * 24 + shengyuH;
    if (shengyuH <= 9) {
      shengyuH = '0' + shengyuH;
    }
    if (shengyuM <= 9) {
      shengyuM = '0' + shengyuM;
    }
    S += 1;
    if (S <= 9) {
      S = '0' + S;
    }

    this.setData({
      hours: shengyuH,
      minutes: shengyuM,
      second: S
    });
  }
})