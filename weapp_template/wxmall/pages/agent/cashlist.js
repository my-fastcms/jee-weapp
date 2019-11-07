var app = getApp();
Page({
  data: {
      inputShowed: false,
      cashRcds:[],
      param:{},
      page:1,
      endFlag:false,
      loadingFlag:false
  },
  onLoad: function (cate) {
    var that = this;
    wx.getSystemInfo({
      success: function (res) {
          //设置scroll-view 的高度
          var scollHeight = res.screenHeight - 90-65; 
          that.setData({
              scollHeight:scollHeight,
              cashRcds:[]
            }
          );
          that.getCashRcds(that.data.page);
      }
    })      
  },

  getCashRcds:function(page){
    if(this.data.endFlag){
      return;
    }
    var that = this;
    that.data.page=page;
    var req={}
    req.page=page;
    that.data.loadingFlag = true;
    app.ajaxJson('/agent/getCashRcds', req, function (res) {
      that.data.loadingFlag = false;
      var json = res.data;
      if (json.code != 200) {
        that.data.endFlag = true;
        return;
      }
      if(json.data && json.data.list && json.data.list.length>0){
        that.setData({
          cashRcds: that.data.cashRcds.concat(json.data.list)
        });
      }else {
        that.data.endFlag=true;
      } 
    },function(){
      that.data.loadingFlag = false;
    });
  },

  // 滑动底部加载
  lower: function () {
    // console.log('滑动底部加载', new Date());
    if (!this.data.endFlag && !this.data.loadingFlag) {
       var page = this.data.page + 1;
       this.getCashRcds(page);
    } else {
      wx.showToast({
        title: '已到最后'
      })
    }
  }
})