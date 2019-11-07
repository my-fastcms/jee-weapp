var app = getApp();

Page({
  data: {
    order: null,
    markers: [{
      //iconPath: '/resources/others.png',
      id: 0,
      latitude: 23.099994,
      longitude: 113.324520,
      title:"测试",
      width: 50,
      height: 50
    }, {
        //iconPath: '/resources/others.png',
        id: 1,
        latitude: 22.099994,
        longitude: 113.324520,
        width: 50,
        height: 50
      }, {
        //iconPath: '/resources/others.png',
        id: 0,
        latitude: 30.099994,
        longitude: 113.324520,
        title: "测试",
        width: 50,
        height: 50
      }, {
        //iconPath: '/resources/others.png',
        id: 1,
        latitude: 22.099994,
        longitude: 113.324520,
        width: 50,
        height: 50
      }],
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor')
  },

  onLoad: function (param) {
    if (!param.id) {
      return;
    }
    var that=this;
    var req={};
    req.orderId=param.id;
    app.ajaxJson('/order/detail', req, function (res) {      
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      that.setData({
        order: json.data
      });
    });
  },
  
  cancelOrder:function(){
    var that=this;
    wx.showModal({
      title: '提示',
      content: '确定要取消订单？',
      success: function (conf) {
        if (conf.confirm) {   
          var req={};
          req.orderId=that.data.order.orderId;
          app.ajaxJson('/order/cancel', req, function (res) {
            var json = res.data;
            if (json.code != 200) {
              wx.showModal({
                title: '提示',
                showCancel: false,
                content: '取消失败！'
              });
              return;
            }
            wx.navigateTo({
              url: '../order/order'
            })
          });
        }
      } 
    })     
  },

  confirmOrder: function(){
    var that = this;
    wx.showModal({
      title: '提示',
      content: '请确保收到货并查看货物质量后签收,否则可能人财两空哦,确定要签收订单吗？',
      success: function (conf) {
        if (conf.confirm) {
          var req = {};
          req.orderId = that.data.order.orderId;
          app.ajaxJson('/order/confirm', req, function (res) {
            var json = res.data;
            if (json.code != 200) {
              wx.showModal({
                title: '提示',
                showCancel: false,
                content: '签收订单失败！'
              });
              return;
            }
            wx.navigateTo({
              url: '../order/order'
            })
          });
        }
      }
    })
  },
  //撤销退款
  cancelRefund: function(){
    var that = this;
    wx.showModal({
      title: '提示',
      content: '确认撤销退款吗？',
      success: function (conf){
        if(conf.confirm){
          var req = {};
          req.orderId = that.data.order.orderId;
          app.ajaxJson('/order/cancelRefund', req, function(res){
            var json = res.data;
            if (json.code != 200) {
              wx.showModal({
                title: '提示',
                showCancel: false,
                content: '撤销退款失败！'
              });
              return;
            }
          });
          wx.navigateBack({
            url: '../order/refundOrder',
          })
        }
      }
    });
  },
  refundOrder: function(){
    var that = this;
    console.log("===orderId:" + that.data.order.orderId);
    wx.showModal({
      title: '提示',
      content: '确认申请退款吗？',
      success: function (conf) {
        if (conf.confirm) {
          var req = {};
          req.orderId = that.data.order.orderId;
          app.ajaxJson('/order/wantRefund', req, function (res) {
            var json = res.data;
            if (json.code != 200) {
              wx.showModal({
                title: '提示',
                showCancel: false,
                content: '申请退款失败！'
              });
              return;
            }
            wx.navigateTo({
              url: '../order/order'
            })
          });
        }
      }
    })
  },

  toProductDetail: function(e){
    var param = e.currentTarget.dataset;
    var id = param["id"];
    wx.navigateTo({
      url: '../product/detail/detail?id=' + id
    })
  },

  wxPrepay: function (orderId) {
    wx.showLoading({
      mask: true,
      title: '正在提交微信支付数据，请稍侯...',
    });
    var req = {};
    req.orderId = this.data.order.orderId;
    app.ajaxJson('/pay/wxAppPrepareToPay', req, function (res) {
      if (res.data.code == 200) {
        wx.hideLoading();
        let wxPay = res.data.data;
        wx.requestPayment({
          'timeStamp': wxPay.timeStamp,
          'nonceStr': wxPay.nonceStr,
          'package': wxPay.packageValue,
          'signType': wxPay.signType,
          'paySign': wxPay.paySign,
          'success': function (rx1) {
            wx.showModal({
              showCancel: false,
              title: '提示',
              content: '支付成功！',
              success: function (rx2) {
                wx.navigateTo({
                  url: '../order/order'
                })
              }
            })
          },
          'fail': function (rx) {
            var msg = '支付失败！';
            if (rx.errMsg == 'requestPayment: fail cancel') {
              msg = '取消支付！'
            }
            wx.showModal({
              showCancel: false,
              title: '提示',
              content: msg
            });
          }
        })

      } else {
        wx.hideLoading();
        wx.showModal({
          title: '提示',
          showCancel: false,
          content: '支付失败！'
        });

      }

    },
      function () {
        wx.hideLoading();
        wx.showModal({
          title: '提示',
          showCancel: false,
          content: '支付失败！'
        });

      }
    );
  }
})  