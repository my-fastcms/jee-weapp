var app = getApp();
Page({
  data: {
    detail: {},
    detail_error : "",
    img_domain : "",
    items:'',
    receiver:null,
    memo:'',
    flag:1,
    seckillProductId: null,
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor'),
    other_bgcolor: wx.getStorageSync('other_bgcolor')
  },
  
  onShow: function (){
      this.setData({  //从选择地址页面back，更新
        receiver: app.data.receiver || null,
        shop:app.data.shop || null,
        city: app.data.city || null
      })
  },
  onLoad: function (param) {
   app.data.receiver=null; 
   var that = this;

   //flag值说明:2表示开团，3表示参团
   that.setData({
     flag : param.flag || null,
     groupId : param.groupId || null,
     bargainUserId: param.bargainUserId || null,
     seckillProductId: param.seckillProductId || null
  });

  console.log("==============flag:" + param.flag + ",groupId:" + that.data.groupId)

   var receiverId="";
   if (param.receiverId){ 
     receiverId = param.receiverId;
   }

  var url = "/pay/balance";
    if (param.flag && (param.flag == 2 || param.flag == 3)){
      url = "/pay/gbalance";
  }

    if (param.flag == 5){
      url = "/pay/sbalance";
  }
  //砍价订单
  if (param.flag == 4){
    var bargainUserId = param.bargainUserId;

    var reqs = {};
    if (receiverId != null){
      reqs.receiverId = receiverId;
    }
    reqs.bargainUserId = bargainUserId;
    app.ajaxJson("/pay/bargainBalancePro", reqs, function (res) {
      var json = res.data;
      //console.log(json);
      if (json.code != 200) {
        if (json.data.error != null) {
          json.msg = json.data.error;
        }

        wx.showModal({
          showCancel: false,
          title: '错误',
          content: json.msg ? json.msg : "500",
        })
        return;
      }
      app.data.receiver = json.data.receiver;
      that.setData({
        img_domain: json.data.img_domain,
        detail: json.data,
        receiver: json.data.receiver || "",
        shop: json.data.shop || "",
        city: json.data.city || "",
        items: param.items || "",
        detail_error: json.data.error || ""
      });
     
    })
    return;
  }

   //flag标记是否原价购买，还是拼团购买，flag指为2代表拼团购买
   app.ajaxJson(url, {items: param.items, receiverId: receiverId}, function (res) {
      var json = res.data;
      //console.log(json);
      if (json.code != 200) {
        if(json.data.error !=null){
          json.msg = json.data.error;
        }

        wx.showModal({
          showCancel: false,
          title: '错误',
          content: json.msg ? json.msg : "500",
        })
        return;
      }
      app.data.receiver = json.data.receiver;
      that.setData({
        img_domain : json.data.img_domain,
        detail: json.data,
        receiver: json.data.receiver || "",
        shop: json.data.shop || "",
        city:json.data.city || "",
        items: param.items,
        detail_error: json.data.error || ""
      });
   })
  },
  created: function (e){
    var formId = e.detail.formId || null;//用于模板消息推送
    console.log("===============formId:" + formId);
    var that = this;
    if ((!that.data.receiver && !that.data.shop) || (that.data.receiver == null && that.data.shop == null)){
      wx.showToast({
        title: '请填写收货地址或门店地址'
      })
      return;
    }
    if (that.data.city && !that.data.city.id ){
      wx.showToast({
        title: '请填写配送门店'
      })
      return;
    }
    wx.showLoading({
      mask: true,
      title: '正在提交订单数据，请稍侯...',
    });
    var items=that.data.items;
    var receiverId = that.data.receiver ? that.data.receiver.id : null;
    var shopId = that.data.shop ? that.data.shop.id : null;
    var cityId = that.data.city ? that.data.city.id : null;
    var memo=that.data.memo;

    //flag值说明:2表示开团，3表示参团
    var submitUrl = "/order/create";
    if(that.data.flag ==2){
      //拼团(开团)
      submitUrl = "/order/gcreate";
    }else if(that.data.flag == 3){
      submitUrl = "/order/joinGroup";//参团
      if(that.data.groupId == null || that.data.groupId ==""){
        wx.showModal({
          title: '提示',
          content: '参团失败，groupId is null',
        })
        return;
      }
    }else if(that.data.flag == 4){
      //砍价订单
      submitUrl = "/order/bargainCreate";
    }else if(that.data.flag == 5) {
      //秒杀
      submitUrl = "/order/sreate";
    }

    app.ajaxJson(submitUrl, { items: items, receiverId: receiverId, shopId: shopId, memo: memo, cityId: cityId, groupid: that.data.groupId, formid: formId, bargainUserId: that.data.bargainUserId, seckillProductId: that.data.seckillProductId }, 
    function (res) {
      wx.hideLoading();
      var json = res.data;
      if (json.code != 200) {
        wx.showModal({
          showCancel: false,
          title: '订单创建错误！',
          content: json.msg ? json.msg : "500",
        })
        return;
      }
     //pay....................
      that.wxPrepay(res.data.data)

    },function(){
      wx.hideLoading();
      wx.showModal({
        showCancel: false,
        title: '订单创建错误！',
        content: json.msg ? json.msg : "500",
      })
    })
  },
  bindTextAreaBlur: function (e) {
    //console.log(e.detail.value);
    this.setData({
      memo: e.detail.value
    })
  },
  addAddress: function(e){
    var that=this;
    if (this.data.city) {
      wx.navigateTo({
        url: '../address/location'
      });
    }else{
      wx.navigateTo({
        url: '../address/select'
      });
    }
  },
  selectAddress:function(e){
    //address id 
    var id =e.currentTarget.id;  
    if(this.data.city){
      wx.navigateTo({
        url: '../address/location?id=' + id
      });
    }else{
      wx.navigateTo({
        url: '../address/select?id=' + id
      });
    }
  },
  addShop: function (e) {    
    wx.navigateTo({
      url: '../address/shop'
    });
  },
  selectShop: function (e) {
    var id = e.currentTarget.id;
    wx.navigateTo({
      url: '../address/shop?id=' + id
    });
  },
  addCity: function (e) {
    wx.navigateTo({
      url: '../address/city'
    });
  },
  selectCity: function (e) {
    var id = e.currentTarget.id;
    wx.navigateTo({
      url: '../address/city?id=' + id
    });
  },
  wxPrepay: function (orderId) {
    wx.showLoading({
      mask: true,
      title: '正在提交微信支付数据，请稍侯...',
    });
   var req={};
   req.orderId = orderId;
    app.ajaxJson('/pay/wxAppPrepareToPay',req,function(res){
        if (res.data.code == 200) {
          wx.hideLoading();
          let wxPay = res.data.data;
          wx.requestPayment({
            'timeStamp': wxPay.timeStamp,
            'nonceStr': wxPay.nonceStr,
            'package': wxPay.packageValue,
            'signType': wxPay.signType,
            'paySign': wxPay.paySign,
            'success': function (res) {
              wx.showModal({
                showCancel: false,
                title: '提示',
                content: '支付成功！',
                success: function (res) {
                  wx.navigateBack({
                    delta: 100
                  })
                }
              })
            },
            'fail': function (res) {
              var msg = '支付失败！请到‘个人->订单’再支付。';
              if (res.errMsg == 'requestPayment: fail cancel') {
                msg = '取消支付！请到‘个人->订单’再支付。'
              }
              wx.showModal({
                showCancel: false,
                title: '提示',
                content: msg,
                success: function (res) {
                  wx.navigateBack({
                    delta: 100
                  })
                }
              });
            }
          })

        } else {
          wx.showModal({
            title: '提示',
            showCancel: false,
            content: '支付失败！请到‘个人->订单’再支付。',
            success: function (res) {
              wx.navigateBack({
                delta: 2
              })
            }
          });
        }
      },
      function() {
        wx.hideLoading();
        wx.showModal({
          title: '提示',
          showCancel: false,
          content: '支付失败！请到‘个人->订单’再支付。',
          success: function (res) {
            wx.navigateBack({
              delta: 2
            })
          }
        });
      }
    );
  }
})