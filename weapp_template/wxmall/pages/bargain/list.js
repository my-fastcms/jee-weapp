var app = getApp();
Page({
  data: {
    multigroup: [],
    param: {},
    page: 1,
    endFlag: false,
    loadingFlag: false,
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor')
  },
  onLoad: function (cate) {
    var that = this;
    wx.getSystemInfo({
      success: function (res) {
        //设置scroll-view 的高度
        var scollHeight = res.screenHeight - 90 - 65;
        that.setData({
          scollHeight: scollHeight,
          multigroup: []
        }
        );
        if (cate && cate.id) {
          that.data.param.categId = parseInt(cate.id);
        }
        that.getMultiGroup(that.data.page);
      }
    })
  },
  getMultiGroup: function (page) {
    if (this.data.endFlag) {
      return;
    }
    var that = this;
    that.data.page = page;
    var req = {}
    req.page = page;
    that.data.loadingFlag = true;
    app.ajaxJson('/bargain/list', req, function (res) {
      that.data.loadingFlag = false;
      var json = res.data;
      if (json.code != 200) {
        that.setData({
          endFlag: true
        });
        return;
      }
      if (json.data && json.data.length > 0) {
        that.setData({
          multigroup: that.data.multigroup.concat(json.data)
        });
      } else {
        that.setData({
          endFlag: true
        });
      }
    }, function () {
      that.data.loadingFlag = false;
    });
  },
  toDetails: function (e) {
    var param = e.currentTarget.dataset;
    var id = param["id"];
    wx.navigateTo({
      url: 'detail?id=' + id
    })
  },
  onReachBottom: function () {
    if (!this.data.endFlag && !this.data.loadingFlag) {
      var page = this.data.page + 1;
      console.info("===========加载第[" + page + "]页数据");
      this.getMultiGroup(page);
    } else {
      console.info("没有数据了...");
    }
  }
})