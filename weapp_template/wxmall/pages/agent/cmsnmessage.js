var app = getApp()
var util = require('../../utils/util.js')
Page({
  data: {
    agent: null,
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor'),
    other_bgcolor: wx.getStorageSync('other_bgcolor')
  },

  onLoad: function () {

  },

  onShow: function () {
    console.log("===========onshow");
    var that = this;
    app.ajaxJson('/agent/commissionIndex', {}, function (res) {
      var json = res.data;
      if (json.code == 200) {
        that.setData({
          GetMission: json.data.GetMission || 0,
          canGetMission: json.data.canGetMission || 0,
          totalMission: json.data.totalMission || 0
        });
      }
    });
  },
  
  navigateToAgentCmsRcd: function () {
    wx.navigateTo({
      url: '../agent/mslist',
    })
  },

  navigateToAgentMission: function () {
    wx.navigateTo({
      url: '../agent/mission',
    })
  },

  onPullDownRefresh: function () {
    this.onShow();
    wx.stopPullDownRefresh();
  }
})
