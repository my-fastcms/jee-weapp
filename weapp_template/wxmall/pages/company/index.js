var WxParse = require('../../wxParse/wxParse.js');
//获取应用实例
var app = getApp()
Page({
  data: {
  },

  onLoad: function () {
    var that = this;
    app.ajaxJson('/company/index', {}, function (res) {
      var json = res.data;
      if (json.code != 200) {
        wx.showModal({
          showCancel: false,
          title: '服务器错误',
          content: json.msg ? json.msg : "500",
        })
        return;
      }
      
      that.setData({
        company: json.data
      });
      if(json.data && json.data.comp_desc)
        WxParse.wxParse('article', 'html', json.data.comp_desc, that, 5);
    });
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function (res) {
    var me = this;
    if (res.from === 'button') {
      // 来自页面内转发按钮
      console.log(res.target)
    }
    return {
      title: '这里有好货，购物还省钱，我为' + wx.getStorageSync('authUser').nick_name + '小程序代言',
      path: '/pages/index/index',
      success: function (res) {
        // 转发成功
      },
      fail: function (res) {
        // 转发失败
      }
    }
  },

  navigateToIndex: function () {
    wx.switchTab({
      url: '../index/index'
    })
  }
})
