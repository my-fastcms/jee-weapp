// pages/bargain/bargainProductDetail.js
var app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    detail: {},
    bargainRecDtos: [],
    bargainUserId: null,
    productId: null,
    buyerId: null,
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor'),
    other_bgcolor: wx.getStorageSync('other_bgcolor'),
    index: 1
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (param) {
    var that = this;
 
    var id = param.barProSetId ;
    var productId = param.productId;
    var bargainUserId = param.bargainUserId;
    if(productId != null && bargainUserId != null){
      that.setData({
        productId: productId,
        bargainUserId: bargainUserId
      })
    }
    that.toAppAjax(param);
    //
  },

  toAppAjax: function(param){
    var that = this;
    app.ajaxJson('/bargain/bargainDetail', param, function (res) {
      var json = res.data;
      
      if (json.code != 200) {
        wx.showModal({
          showCancel: false,
          title: '错误',
          content: json.msg ? json.msg : "500",
        })
        return;
      }
      var price = json.data.price;
      var mycut = price - json.data.baragainPrice;
      var lowestMoney = json.data.lowestMoney;

      var cutPrice = price - lowestMoney;
      var currentPrice = (mycut / cutPrice).toFixed(2) * 100;

      var bargainRecDto = json.data.bargainRecDto || null;
      var bargainRecDtos = json.data.bargainRecDtos || null;
      var buyerId = json.data.buyerId;

      //当前价和底价
      var baragainPrice = json.data.baragainPrice || null;
      var lowestMoney = json.data.lowestMoney || null;
      if (baragainPrice == lowestMoney) {
        that.setData({
          flags: 3
        })
      }

      //判断是否为砍价用户本人
      if (bargainRecDto){
        var currentBuyerId = bargainRecDto.buyerId;
        if(currentBuyerId == buyerId){
          that.setData({
            flags: 1
          })
        }else{
          that.setData({
            flags: 2
          })
        }
      }
     
      that.setData({
        detail: json.data,
        bargainRecDtos: bargainRecDtos,
        bargainRecDto: bargainRecDto,
        buyerId: buyerId,
        width: currentPrice
      })
      //
    })
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
  onShareAppMessage: function (e) {
   
    var that = this;
    var bargainUserId = that.data.bargainUserId;
    var productId = that.data.productId;
    var shareObj = {
      title: '快来帮我砍价',
      path: '/pages/bargain/bargainProductDetail?productId='+productId+'&bargainUserId='+bargainUserId,
      success: function (res) {
        //console.log(res);
        // 转发成功
      },
      fail: function (res) {
        //console.log(res);
        // 转发失败
      }
    }
    return shareObj;
  },
  toCallPerson: function(){
    var that = this;
    wx.showToast({
      title: '点击右上角转发分享砍价',
      icon: 'none',
      duration: 2000
    })
  },
  //帮砍一刀
  toHelpBargainProduct: function(e){
    var that = this;
    var bargainUserId = that.data.bargainUserId;
    var productId = that.data.productId;
    //可用于推送消息
    var formId = that.data.formId;

    var param = {};
    param.bargainUserId = bargainUserId;
    param.productId = productId;
    param.formId = formId;
    param.flag=1;
    that.toAppAjax(param);
   
  },
  formSubmit: function (e) {
    var that = this;
    var formId = e.detail.formId || null;//用于模板消息推送
    console.log("===============formId:" + formId);
    this.setData({
      formId: formId
    });
  },
  //我也要
  toCutBargainProduct: function(){
    wx.redirectTo({
      url: "detail?id=" + this.data.productId,
    })
  },
  //去结算,支付
  payBargainOrder: function(param){

    var bargainUserId = param.currentTarget.dataset.bargainuserid;
 
    wx.navigateTo({
      url: '../pay/jiesuan?flag=' + 4 + '&bargainUserId=' + bargainUserId
    })
  },
  // 切换
  checkTap(e) {
    this.setData({
      index: e.currentTarget.dataset.index
    })
  },
  // 进入店铺
  checkTapp(e) {
  
    wx.switchTab({
      url: '../../pages/index/index',
    }),
    this.setData({
      index: e.currentTarget.dataset.index
    })
  }
})