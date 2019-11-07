
var app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    //order: {},
    orders: []
  },
  //跳转至对应的订单详情
  toProductDetail: function (e) {
    var param = e.currentTarget.dataset;
    var id = param["id"];
    wx.navigateTo({
      url: '../product/detail/detail?id=' + id
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (param) {
    var that = this;
    if (!param.id) {
      return;
    }
    var req = {};
    req.orderId = param.id;
    //订单ID查找对应信息
    app.ajaxJson('/agent/getOrders', req, function (res) {
      var json = res.data;
      if(json.code == 200){
        that.setData({
          orders: json.data,
          //order: json.data[0]
        })
      }else{
        wx.showModal({
          title: '错误',
          content: '操作失误:'+json.errMsg,
        })
      }
    })
  },

})