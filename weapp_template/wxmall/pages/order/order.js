var app = getApp();
Page({
  data: {
    tabs: ["待付款", "待成团", "待发货", "待收货", "已完成"],
    activeIndex: 0,
    param: {},
    page: 1,
    orders:[],
    endFlag: false,
    loadingFlag: false
  },
  onLoad: function (param) {
    var that = this;
    var activeIndex = 0;
    if (param.statusIndex){
      activeIndex = parseInt(param.statusIndex);
    } 
    wx.getSystemInfo({
      success: function (res) {
        var scollHeight = res.screenHeight - 40;
        that.setData({
          scollHeight: scollHeight,
          orders: [],
          activeIndex: activeIndex, 
          endFlag: false,
          loadingFlag: false
        });
        that.getOrders(that.data.page);
      }
    })    
  },
  tabClick: function (e) {
    // if (this.data.loadingFlag){
    //   return;
    // }
    this.data.activeIndex = e.currentTarget.id;
    this.setData({
      activeIndex: e.currentTarget.id,
      page: 1,
      endFlag: false,
      loadingFlag: false,
      orders: []
    });
    this.getOrders(this.data.page);
  },
  getOrders:function(page){
    if (this.data.endFlag) {
      return;
    }
    var that = this;
    that.data.page = page;
    var req = {check:true}
    req.page = page;
    if (that.data.activeIndex==0){ //待支付
      req.payment_status='unpaid';
      req.order_status = 'unconfirmed';
      req.shipping_status = 'unshipped';
    } else if (that.data.activeIndex == 1) {//拼团订单，待成团
      req.order_status = 'unconfirmed';
      req.payment_status = 'paid';
      req.order_type = 2;
      req.group_status = "grouping";
      req.shipping_status = 'unshipped';
    } else if (that.data.activeIndex == 2) {//待发货
      req.order_status = 'unconfirmed';
      req.payment_status = 'paid';
      //req.group_status = "success";   //已成团，也待发货
      req.shipping_status = 'unshipped';
    } else if (that.data.activeIndex == 3) {//已发货，待收货
      req.order_status='unconfirmed';
      req.payment_status='paid';
      req.shipping_status='shipped';
    } else if (that.data.activeIndex == 4){//交易完成
      req.order_status = 'completed';
      req.payment_status = 'paid';
      req.shipping_status = 'shipped';
    }else {
      req.payment_status = 'unpaid';
      req.order_status = 'unconfirmed';
      req.shipping_status = 'unshipped';
    }
    
    that.data.loadingFlag=true;
    app.ajaxJson('/order/list', req, function (res) {
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

  // 滑动底部加载
  lower: function () {
    // console.log('滑动底部加载', new Date());
    if (!this.data.endFlag && !this.data.loadingFlag) {
      var page = this.data.page + 1;
      this.getOrders(page);
    } else {
      wx.showToast({
        title: '已到最后'
      })
    }
  },

  toDetail: function (e) {
    var param = e.currentTarget.dataset;
    var id = param["id"];
    wx.navigateTo({
      url: '../order/detail?id=' + id
    })
  },
  toReview:function(e){
    var productId = e.currentTarget.dataset.productid;
    var orderId=e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '../order/review?orderId='+orderId+'&productId=' + productId
    })
    return false;
  }

})    