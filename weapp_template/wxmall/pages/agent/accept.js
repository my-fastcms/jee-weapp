// pages/agent/share.js
var app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    agent:null,
    userInfo:null
  },

  navigateToHome: function(){
    wx.switchTab({
      url: '/pages/index/index'
    })
  },

  accept: function(){
    var me = this;
    wx.showModal({
      title: '提示',
      content: '确定接受' + me.data.userInfo.nickname + "的邀请吗?",
      success: function (res) {
        if (res.confirm) {
          app.ajaxJson("/agent/accept", { parentId: me.data.agent.id}, function (res) {
            var json = res.data;
            if (json.code != 200) {
              wx.showModal({
                title: '错误',
                content: '操作失败,' + json.msg
              })
            } else {
              wx.showToast({
                title: '接受成功，快去购物吧...',
                duration: 3000,
                success: function () {
                  wx.switchTab({
                    url: '/pages/index/index'
                  })
                }
              })
            }
          });
        }
      }
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var me = this;
    app.ajaxJson("/agent/getAgent", { agentId: options.parentid}, function (res) {
      var json = res.data;
      if (json.code == 200) {
        me.setData({
          agent: json.data.agent,
          userInfo: json.data.agentUser
        });
      }
    });
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
  
  }
})