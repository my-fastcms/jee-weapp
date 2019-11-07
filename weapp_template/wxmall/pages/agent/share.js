// pages/agent/share.js
var app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    agent:null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var me = this;
    me.setData({
      userInfo: app.data.userInfo
    });
    app.ajaxJson("/agent/getAgent", {}, function (res) {
      var json = res.data;
      if (json.code == 200) {
        me.setData({
          agent: json.data
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
      title: "这里有好货，购物还省钱，我为" + wx.getStorageSync('authUser').nick_name+"代言",
      path: "/pages/agent/accept?parentid="+me.data.agent.id,
      success: function (res) {
        // 转发成功
      },
      fail: function (res) {
        // 转发失败
      }
    }
  },

  test: function(){
    wx.navigateTo({
      url: '../agent/accept?parentid='+this.data.agent.id
    })
  }
})