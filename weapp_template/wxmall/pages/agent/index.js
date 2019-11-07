var app = getApp()
var util = require('../../utils/util.js')
Page({
  data: {
    agent: null,
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor'),
    other_bgcolor: wx.getStorageSync('other_bgcolor'),
    formIds: [],
    param: {}
  },

  onLoad: function () {

  },

  onShow: function () {
    console.log("===========onshow");
    var that = this;
    app.ajaxJson('/agent/index', {}, function (res) {
      var json = res.data;
      if (json.code == 200) {
        that.setData({
          agent: json.data.agent || null,
          GetMission: json.data.GetMission || 0,
          canGetMission: json.data.canGetMission || 0,
          totalMission: json.data.totalMission || 0,
          noGetMission: json.data.noGetMission || 0,
          ordersCount: json.data.ordersCount || 0,
          teamCount: json.data.teamCount || 0,
          rankName: json.data.rankName || null
        });
      }
    });
  },

  navigateToAgentCmsRcd: function () {
    wx.navigateTo({
      url: '../agent/cmsnmessage',
    })
  },

  navigateToAgentPoster: function (e) {
    if (e.detail.errMsg == "getUserInfo:ok") {
      var params = {}
      app.ajaxJson('/agent/getPoster', params, function (res) {
        var json = res.data;
        if (json.code != 200) {
          return;
        }
        if (json.data && json.data.length > 0) {
          wx.previewImage({
            urls: [json.data]
          })
        }
      });
    }  
  },

  navigateToAgentCashRcd: function () {
    wx.navigateTo({
      url: '../agent/cashlist',
    })
  },

  navigateToCanGetMission:function(){
    wx.navigateTo({
      url: '../agent/mission',
    })
  },

  navigateToAgentOrder: function () {
    wx.navigateTo({
      url: '../agent/order',
    })
  },

  navigateToAgentTeam: function () {
    wx.navigateTo({
      url: '../agent/team',
    })
  },

  formSubmit: function (e) {
    var that = this;
    var formId = e.detail.formId || null;//用于模板消息推送
    that.data.formIds.push(formId);
    var params = {}
    params.formIds = JSON.stringify(this.data.formIds)
    console.log("===============formId:" + params.formIds);
    app.ajaxJson('/agent/saveFormId', params, function (res) {
      var json = res.data;
      if (json.code == 200) {
        that.setData({
          formIds : []
        });
      }else{
        console.log(json.data);
      }
    });
  },

  onPullDownRefresh: function () {
    this.onShow();
    wx.stopPullDownRefresh();
  }
})
