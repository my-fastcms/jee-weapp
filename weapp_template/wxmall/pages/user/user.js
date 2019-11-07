var app = getApp()
var util = require('../../utils/util.js')
Page({
  data: {
    needAuth: true,
    agent:null,
    payedCount:0,
    shippedCount:0,
    needAudit: 1,
    config:0,
    unpayedCount:0,
    complateCount:0,
    groupingCount:0,
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor'),
    other_bgcolor: wx.getStorageSync('other_bgcolor')
  },

  onLoad: function (option){
    if (option.scene) {
      var openId = decodeURIComponent(option.scene);
      app.data.shareOpenId = openId;
      app.data.sceneEven = false;
      wx.redirectTo({
        url: '/pages/user/auth',
      })
    }
  },

  onShow: function () {
    console.log("===========onshow");
    
    var that = this;
    that.setData({ needAuth: wx.getStorageSync('needAuth') });

    that._getUserIndexData(that);

    if (app.data.sceneEven) {
      app.data.sceneEven = null;
      app.ajaxJson('/agent/scene', { openId: app.data.shareOpenId }, function (res) {
        var json = res.data;
        console.log(app.data.sceneEven+"=====================");
        if (json.code != 200) {
          wx.showModal({
            title: '错误',
            content: json.msg
          })
        }
        that._getUserIndexData(that);
      });
    }   
    
  },

  _getUserIndexData: function(that){
    app.ajaxJson('/user/index', {}, function (res) {
      var json = res.data;
      if (json.code == 200) {
        that.setData({
          agent: json.data.agent || null,
          needAudit: json.data.needAudit || 1,
          totalMission: json.data.totalMission || 0,
          canGetMission: json.data.canGetMission || 0,
          cachMission: json.data.cachMission || 0,
          payedCount: json.data.payedCount || 0,
          shippedCount: json.data.shippedCount || 0,
          unpayedCount: json.data.unpayedCount || 0,
          complateCount: json.data.complateCount || 0,
          groupingCount: json.data.groupingCount || 0
        });
      }
    });
  },

  navigateToTask: function () {
    wx.navigateTo({
      url: '../task/index'
    });
  },
  navigateToRefundOrder: function(){
    wx.navigateTo({
      url: '../order/refundOrder',
    })
  },
  //我的砍价商品
  navigateToMyBargainProduct: function () {
    wx.navigateTo({
      url: '../bargain/myList',
    })
  },
  navigateToshopcart: function(){
    wx.switchTab({
      url: '../cart/cart',
    })
  },
  navigateToAddress: function () {
    wx.navigateTo({
      url: '../address/select?fromPage=user'
    });
  },
  navigateToAgent: function () {
    var that = this;
    var agent = that.data.agent;
    if (agent != null && agent.status != 1) {
      wx.showModal({
        title: '提示',
        content:"正在审核中"
      })
      return false;
    }
    wx.navigateTo({
      url: '../agent/apply',
    })
  },
  navigateToAgentShare: function () {
    var that = this;
    var needAudit = that.data.needAudit;
    if (that.data.config == 1){
      wx.showModal({
        content: "商家尚未开启分销功能"
      })
      return;
    }
    var agent = that.data.agent;
    if (agent != null){
      wx.navigateTo({
        url: '../agent/index',
      })      
    }else{
      wx.showModal({
        title: '提示',
        content: '确认成为销售员进行推广赚取佣金吗?',
        success: function (res) {

          if (res.confirm) {
            app.ajaxJson('/agent/autoSave', {}, function (res) {
              var json = res.data;
              if (json.code != 200) {
                wx.showModal({
                  title: '错误',
                  content: json.msg
                })
                return false;
              }
              wx.navigateTo({
                url: '../agent/index',
              }) 
            });
          }

        }
      })
    }
  },
  navigateToAgentMission: function () {
    wx.navigateTo({
      url: '../agent/mission',
    })
  },
  navigateToAgentCmsRcd: function () {
    wx.navigateTo({
      url: '../agent/mslist',
    })
  },
  navigateToAgentCashRcd: function () {
    wx.navigateTo({
      url: '../agent/cashlist',
    })
  },
  navigateToAboutus: function(){
    wx.navigateTo({
      url: '../company/index',
    })
  },
  navigateToCard: function(){
    wx.showToast({
      title: '功能开发中...',
    })
  },
  onPullDownRefresh: function () {
    this.onShow();
    wx.stopPullDownRefresh();
  },
  /**
   * 点击授权登录
   */
  bindGetUserInfo: function (e) {
    var that = this
    if (e.detail.userInfo) {
      that._login();
    }
  },

  /**
   * 登录
   */
  _login: function () {
    var me = this;
    util.login(app, function (res) {
      me.setData({
        userInfo: res,
        needAuth: false,
        buyer: res || null,
        buyerScore: res.score || 0,
        state: 'ok'
      });
      // wx.navigateBack({
      //   delta: 1
      // }) 
      wx.setStorageSync('needAuth', false);
      me._getUserIndexData(me);
    });
  }
})
