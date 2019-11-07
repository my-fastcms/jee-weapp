var app = getApp();
Page({
  data: {
    order: null,
    product:null,
    orderId:null,
    productId:null,
    commentType:'3',
    memo:''
  },
  onLoad: function (param) {
    this.data.orderId = param.orderId;
    this.data.productId = param.productId;
    this.getOrderAndReviews();
  },

 getOrderAndReviews: function(){
   var that = this;
   var req = {};
   req.orderId = this.data.orderId ;
   req.productId = this.data.productId;
   app.ajaxJson('/order/getOrderInfo', req, function (res) {
     var json = res.data;
     if (json.code != 200) {
       return;
     }
     var orderDto = json.data.orderDto;
     var reviews = json.data.reviews || {};

     var product = null;
     var productId = parseInt(that.data.productId);
     if (orderDto.orderItems && orderDto.orderItems.length > 0) {
       for (var i = 0; i < orderDto.orderItems.length; i++) {
         if (orderDto.orderItems[i].productId == productId) {
           product = orderDto.orderItems[i];
           break;
         }
       }
     }
     that.setData({
       product: product,
       order: orderDto,
       reviews: reviews || {}
     });
   });
 },

  selectCommentType: function (e) {
    var commentType = e.currentTarget.dataset.type;
    this.setData({
      commentType: commentType
    });
  },
  bindTextAreaBlur: function (e) {
    //console.log(e.detail.value);
    this.setData({
      memo: e.detail.value
    })
  },
  saveReview:function(){
    var me = this;
    var req = {};
    req.orderId =this.data.orderId;
    req.productId=this.data.productId;
    req.score = this.data.commentType;
    req.content = this.data.memo;

    if(this.data.memo == null || this.data.memo == ""){
      wx.showToast({
        title: '请输入评价内容',
      })
      return;
    }
    wx.showLoading({
      mask: true,
      title: '正在提交数据，请稍侯...',
    })
    app.ajaxJson('/order/saveReview', req, function (res) {
      wx.hideLoading();
      var json = res.data;
      if (json.code == 200) {
        wx.showModal({
          title: '提示',
          content: '评价成功，感谢您的支持',
          success: function(){
            me.getOrderAndReviews();
          }
        })
      }else{
        wx.showToast({
          title: '保存评介出错！',
        })
      }
    },function(){
      wx.hideLoading();
    });
  }
})  