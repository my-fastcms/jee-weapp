// pages/user/auth.js
var app = getApp()
var util = require('../../utils/util.js')
Page({

  /**
   * 页面的初始数据
   */
  data: {
  
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
   var url =  options.url;
    var bargainUserId = options.bargainUserId;
    var productId = options.productId;
   this.setData({
     url: url || null,
     bargainUserId: bargainUserId || null,
     productId: productId || null
   })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
  
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
   
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
  
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
  
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
  
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
  
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
  
  },

  onGetUserInfo: function (e) {
    var me = this;
    var url = me.data.url;
    var bargainUserId = me.data.bargainUserId;
    var productId = me.data.productId;
 
    if (app.data.sceneEven != null && !app.data.sceneEven){
      app.data.sceneEven = true;
    }
    if (e.detail.errMsg == "getUserInfo:ok") {
      wx.checkSession({
        success: function () {
          //session_key 未过期，并且在本生命周期一直有效;说明微信服务器登录态有效
          console.info("================user.js is logining....");
          var userInfo = e.detail.userInfo;
          //对用户数据进行签名校验
          var req = {};
          req.rawData = e.detail.rawData;
          req.signature = e.detail.signature;
          req.encryptedData = e.detail.encryptedData;
          req.iv = e.detail.iv;
          app.ajaxJson("/user/check", req, function (res) {
            var userRes = res.data;
            if (userRes.code != 200) {
              //此处check user不成功，说明自身服务器session失效
              //故重新发起登陆，会在微信服务器sessionKey不失效的情况下，刷新sessionKey
              console.info("====================user/check fail, self session is timeout relogin");
              me._login();
            } else {
              console.info("===============check user success");
              app.data.userInfo = userInfo;
              me.setData({
                userInfo: app.data.userInfo
              });
              if (url!=null && bargainUserId!=null && productId!=null){
                wx.redirectTo({
                  url: '/pages' + url + '?productId=' + productId+'&bargainUserId=' + bargainUserId,
                })
                return;
              }
              wx.switchTab({
                url: '../user/user',
              })
            }
          });
        },
        fail: function () {
          // session_key 已经失效，需要重新执行登录流程
          console.log("================logout===========")
          //重新登录
          me._login();
        }
      })
    }
  },

  onCancelAuth: function(){
    console.log("================onCancelAuth");
    wx.switchTab({
      url: '/pages/index/index',
    })
  },

  _login: function () {
    var me = this;

    //砍价
    var url = me.data.url;
    var bargainUserId = me.data.bargainUserId;
    var productId = me.data.productId;

    util.login(app, function (res) {
      me.setData({ userInfo: res });

      //砍价
      if (url != null && bargainUserId != null && productId != null) {
        wx.redirectTo({
          url: '/pages/bargain/bargainProductDetail?productId=' + productId + '&bargainUserId=' + bargainUserId,
        })
        return;
      }

      wx.switchTab({
        url: '/pages/user/user'
      });
      // wx.navigateBack({
      //   delta: 1
      // });
    });
  }

})