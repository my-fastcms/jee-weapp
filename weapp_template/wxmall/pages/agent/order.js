// pages/agent/personal/order.js
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    tabs: ["全部", "未结算",  "已结算"],
    orders: [
    ],
    activeIndex: 0,
    //index: 0,
    page: 1,
    endFlag: false,
    loadingFlag: false

  },
  tabClick: function (e) {
    var that = this;
    var activeIndex = e.currentTarget.id;
    this.setData({
      orders: [],
      index: activeIndex,
      activeIndex: activeIndex,
      page: 1,
      endFlag: false,
      loadingFlag: false
    });
   
    this.getDistributionOrders(this.data.page);
  },
 
  

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (param) {
    var that = this;
    var activeIndex = 0;
    wx.getSystemInfo({
      success: function (res) {
        var scollHeight = res.screenHeight - 40;
        that.setData({
          scollHeight: scollHeight,
          orders: [],
          activeIndex: activeIndex,
          page: 1,
          endFlag: false,
          loadingFlag: false
        });
        that.getDistributionOrders(that.data.page);
      }
    }) 
  },

  //获取所有分销订单列表
  getDistributionOrders(page){
    if (this.data.endFlag) {
      return;
    }
    var that = this;
    that.data.page = page;
    var statusIndex = that.data.activeIndex;
    var req = {};
    // 参数不传查所有 1 未结算 2 已结算
    if (statusIndex > 0){
      req.status = that.data.activeIndex; 
    }
    req.page = page;
    that.data.loadingFlag = true;
    app.ajaxJson("/agent/getOrders",req, function (res) {
      that.data.loadingFlag = false;
      var json = res.data;
      if (json.code != 200) {
        that.data.endFlag = true;
        return;
      } 
      if (json.data && json.data.length > 0) {
        that.setData({
          orders: that.data.orders.concat(json.data)
        });
      } else {
        that.data.endFlag = true;
      }
    }, function () {
      that.data.loadingFlag = false;
    });
  },
  //跳转至佣金订单详情
  toCommissionDetail: function(e){
    var param = e.currentTarget.dataset;
    var id = param["id"];
    wx.navigateTo({
      url: '../agent/cmsndetail?id='+id,
    })
  },
  // 滑动底部加载
  lowerview: function () {
    //console.log('滑动底部加载', new Date());
    if (!this.data.endFlag && !this.data.loadingFlag) {
      var page = this.data.page + 1;
      this.getDistributionOrders(page);
    } else {
      wx.showToast({
        title: '已到最后'
      })
    }
  },
  //页面上拉触底事件的处理函数 页面上拉触底事件的处理函数
  onReachBottom: function () {
    this.lowerview();
  },
})