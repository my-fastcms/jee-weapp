// pages/agent/mission.js
var app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    money : null,
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor')
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var me = this;
    app.ajaxJson("/agent/commission", {}, function (res) {
      var json = res.data;
      if (json.code == 200) {
        me.setData({
          totalCommission: json.data.totalCommission,
          cashCount: json.data.cashCount || 1
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

  cashMoney: function (e) {
    var v1 = e.detail.value;
    if (v1 == null || v1 == "") {
      this.setData({
        money: 0
      })
      return;
    }
    var v2 = parseFloat(v1).toFixed(2);
    this.setData({
      money: v2
    })
  },
  
  getCash : function(){
    var that = this;
    var money = that.data.money;

    if (money == null || money <= 0){
      wx.showModal({
        content: '请输入提现金额'
      })
      return;
    };

    app.ajaxJson("/agent/getCash", { money: money }, function (res) {
      var json = res.data;
      if (json.code == 200) {
        wx.showToast({
          title: '提现成功'
        });
        app.ajaxJson("/agent/commission", {}, function (res) {
          var json = res.data;
          if (json.code == 200) {
            that.setData({
              totalCommission: json.data.totalCommission,
              cashCount: json.data.cashCount || 1
            });
          }
        });
      }else{
        wx.showModal({
          title: '提现失败',
          content: json.msg ? json.msg:'系统错误',
        })
      }
    })
  }
})