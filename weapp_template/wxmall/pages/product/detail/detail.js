var WxParse = require('../../../wxParse/wxParse.js');
var app = getApp();
Page({
    data: {
        detail: {},
        cartCount:0,
        reviews:[],
        reviewInitFlag:false,
        page: 1,
        endFlag: false,
        loadingFlag: false,
        specificationArray:[],
        tabs: ["商品详情",  "商品评价"],
        activeIndex: 0,
        num:1,
        minusStatuses: '',
        flag:0,
        totalPrice:0.00,
        attrValueList: [],
        groupings: null,
        navbar_bgcolor: wx.getStorageSync('navbar_bgcolor'),
        other_bgcolor: wx.getStorageSync('other_bgcolor'),
        show_safeguard: wx.getStorageSync('show_safeguard'),
        show_delivery_time: wx.getStorageSync('show_delivery_time'),
        show_compensate: wx.getStorageSync('show_compensate')
        
    },

    onLoad: function (param) {
      this.data.productId = param.id;
      var that = this;
      wx.getSystemInfo({
        success: function (scr) {
          var scollHeight = scr.screenHeight - 90;
          app.ajaxJson('/product/detail', { id: param.id}, function (res) {
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
              if (detail.priceMap["agentPrice"] != null){
                detail.product.price = detail.priceMap["agentPrice"].agentPrice;
              }
              that.setData({
                detail: detail,
                price: detail.product.price,
                totalPrice:0.00,
                specificationArray: [],
                specPriceMap: detail.priceMap,
                groupings: detail.groupings || null,
                cartCount: json.data.cartCount || 0,
                scollHeight: scollHeight,
                res: json.data.res,
                promotionInfo: detail.promotionInfo || null
              });

              WxParse.wxParse('article', 'html', detail.product.introduction, that, 0);
              that.distachAttrValue(that.data.detail); //init attr
              
              var groupList = that.data.groupings;
              if (groupList && groupList.length > 0) {
                for (var i = 0; i < groupList.length; i++) {
                  var t = --groupList[i].expiresIn;
                  var h = Math.floor(t / 60 / 60);
                  var m = Math.floor((t - h * 60 * 60) / 60);
                  var s = t % 60;
                  if (h < 10) h = "0" + h;
                  if (m < 10) m = "0" + m;
                  if (s < 10) s = "0" + s;
                  groupList[i].leftTimeStr = h + ':' + m + ':' + s
                }
                that.setTimeData(groupList);
              }

              //默认单规格情况下
              if (!detail.specifications || detail.specifications.length==0){
                 var price = parseFloat(that.data.price);
                 price=price.toFixed(2);
                 var totalPrice = price* that.data.num;
                 totalPrice = totalPrice.toFixed(2);
                 that.setData({
                   price: price,
                   totalPrice: totalPrice
                 });
              }
          });
        }
      })    
      that.getCartCount();  
    },

    getCartCount:function(){
      var that = this;
      app.ajaxJson('/cart/getCartCount', {}, function (res) {
        var json = res.data;
        if (json.code == 200) {
          var cartCount = json.data;
          that.setData({
            cartCount: cartCount
          });
        }
      });
    },

    tabClick: function (e) {
      this.setData({
        activeIndex: e.currentTarget.id
      });
      if (e.currentTarget.id == 1 && !this.data.reviewInitFlag) {
        this.data.reviewInitFlag=true;
        this.getReviews(this.data.page);
      }
    },

    swiperchange: function(){
        
    },

    setTimeData: function (data) {
      var self = this;
      var groupList = data;
      setInterval(function () {
        for (var i = 0; i < groupList.length; i++) {
          var t = --groupList[i].expiresIn;
          var h = Math.floor(t / 60 / 60);
          var m = Math.floor((t - h * 60 * 60) / 60);
          var s = t % 60;
          if (h < 10) h = "0" + h;
          if (m < 10) m = "0" + m;
          if (s < 10) s = "0" + s;
          groupList[i].leftTimeStr = h + ':' + m + ':' + s
        }
        self.setData({
          groupings: groupList
        })
      }, 1000)
    },

    bindViewTap: function (e) {
      var param = e.currentTarget.dataset;
      var id = param["id"];
      var index=param["index"];
      this.setData({
        selected: id,
        firstIndex:index
      });
    },

    onCartClick: function () {
      wx.reLaunch({
        url: '/pages/cart/cart'
      })
    },

    setModalStatus: function (e) {
      var that = this;
      //清除已选择规格
      var attrValues = this.data.attrValueList;
      for (var k = 0; k < attrValues.length;k++){
        if (attrValues[k].selectedValue && attrValues[k].selectedValue !=null){
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
      var flag = e.currentTarget.dataset.flag ;
      this.setData({
        flag: flag
      });

      var price = 0.00;
      if(flag && flag == 2){
        //拼团情况显示拼团价格（单规格的情况，多规格另外处理）
        if (that.data.detail.specifications == null || that.data.detail.specifications.length<=0){
          price = parseFloat(that.data.detail.groupInfo.collagePrice);
        }
      }else{
        //单独购买或加入购物车
        if (that.data.detail.specifications == null || that.data.detail.specifications.length <= 0) {
          price = parseFloat(that.data.detail.product.price);
        }
      }
      if (that.data.promotionInfo != null){
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
              num : 1
          });
        }
      }.bind(this), 100)
    },

    pay: function (e) {
      var that=this;
      var sflag=true;
      for(var i=0;i<that.data.specificationArray.length;i++){
        if(that.data.specificationArray[i].sfId==''){
          sflag=false;
          break;
        }
      }
      if(!sflag){
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
      if(flag==0){
        that._add2cart(that, productId, quantity, speci);
      }else if(flag==1 || flag ==2){
        var itemsArray = new Array();
        var entity = new Object();
        entity.productId = productId;
        entity.pcount = quantity;
        entity.speci = speci;
        itemsArray.push(entity);
        //console.log(JSON.stringify(itemsArray));
        wx.navigateTo({
          url: '../../pay/jiesuan?flag='+flag+'&items=' + JSON.stringify(itemsArray)
        })
      }
      this.setData({
        showModalStatus: false
      });
    },

    _add2cart: function (that, productId, quantity, speci){
      var req = {};
      req.productId = productId;
      req.quantity = quantity;
      req.speci = speci;
      app.ajaxJson('/cart/addCart', req, function (res) {
        var json = res.data;
        if (json.code != 200) {
          wx.showModal({
            showCancel: false,
            title: '错误',
            content: json.msg ? json.msg : "500",
          })
        } else {
          var cartCount = json.data;
          wx.showToast({
            title: '加入购物车成功',
            icon: 'success',
            duration: 1000
          });
          that.setData({
            cartCount: cartCount
          });
        }
      }); 
    },

    bindMinus: function (e) {
      var num = this.data.num;
      // 如果只有1件了，就不允许再减了
      if (num > 1) {
        num--;
        this.setTotalPrice(num);
      }
    },

    bindPlus: function (e) {
      var num = this.data.num;
      if(num>=99999){
        return;
      }
      // 自增
      num++;
      this.setTotalPrice(num);
    },

    bindManual: function (e) {
      var num = parseInt(e.detail.value);
      if (isNaN(num)){
        num=1;
      }
      // 将数值与状态写回
      if(num>0 && num<100000){
         this.setTotalPrice(num);
      }
    },

    setTotalPrice:function(num){
      var totalPrice='';
        if (!isNaN(this.data.price)){
          totalPrice = this.data.price*num;
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
      if (detail.specifications && detail.specifications.length>0){
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
        specificationArray[index].sfId=sfId;
        specificationArray[index].spvId=spvId;
        var spcs=[];
        for (var p = 0; p < specificationArray.length;p++){
          spcs.push(specificationArray[p].spvId);
        }
        var spckey=spcs.join(",");
        var spec = this.data.specPriceMap[spckey];
        var price=0;
        var totalPrice = '';
        if(spec){
          price = spec.price;
          if (spec.promPrice != null && spec.promPrice !=""){
            price = spec.promPrice;
          }
          if (me.data.flag==2 && spec.collagePrice != null && spec.collagePrice !=""){//拼团
            price = spec.collagePrice;
          }
          if (spec.agentPrice != null && spec.agentPrice != "") {
            price = spec.agentPrice;
          }
          totalPrice = price*this.data.num;
          totalPrice = totalPrice.toFixed(2);
        }
        attrValueList[index].selectedValue = value;
        this.setData({
          price:price,
          totalPrice: totalPrice,
          attrValueList: attrValueList
        });
    }
  },

  // 滑动底部加载
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
      if (json.data.lastPage){
        that.data.endFlag = true;
      }
    }, function () {
      that.data.loadingFlag = false;
    });
  },

  previewImg : function(){
    wx.previewImage({
      //current: '', // 当前显示图片的http链接
      urls: this.data.detail.imageList // 需要预览的图片http链接列表
    })
  },

  onShareAppMessage: function (res) {
    var me = this;
    if (res.from === 'button') {
      // 来自页面内转发按钮
      console.log(res.target)
    }
    return {
      title: "发现好货:" + me.data.detail.product.name,
      path: '/pages/product/detail/detail?id='+me.data.detail.product.id,
      success: function (res) {
        // 转发成功
      },
      fail: function (res) {
        // 转发失败
      }
    }
  },

  goHome : function(){
    wx.switchTab({
      url: '/pages/index/index',
    })
  },

  collect: function(){
    wx.showToast({
      title: '已收藏',
    })
  },

  joinGroup: function (e) {
    var id = e.currentTarget.dataset.groupid;
    console.log("============joinGroup.groupId:" + id)
    wx.navigateTo({
      url: '/pages/group/detail?groupId=' + id ,
    })
  },
  onPullDownRefresh: function () {
    wx.stopPullDownRefresh();
  }
})