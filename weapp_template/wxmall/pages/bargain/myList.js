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
    app.ajaxJson('/bargain/myList', req, function (res) {
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
          multigroup: that.data.multigroup.concat(json.data),
        });
      } else {
        that.setData({
          endFlag: true
        });
      }
      //
      var unTimeMillis = that.data.multigroup;
      if (unTimeMillis) {
        that.setTimeData(unTimeMillis);
      }
    }, function () {
      that.data.loadingFlag = false;
    });
    
  },
  toDetails: function (e) {
    var param = e.currentTarget.dataset;
    var productId = param["id"];
    var bargainUserId = param["baruserid"]
    wx.navigateTo({
      url: 'bargainProductDetail?productId=' + productId + "&bargainUserId=" + bargainUserId
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
  },
  setTimeData: function (data) {
    var that = this;
    var times = data;
    if (times != null) {
      setInterval(function () {
        for (var i = 0; i < times.length; i++) {
          var t = --times[i].endDate;
          var h = Math.floor(t / 60 / 60);
          var m = Math.floor((t - h * 60 * 60) / 60);
          var s = t % 60;
          if (h < 10) h = "0" + h;
          if (m < 10) m = "0" + m;
          if (s < 10) s = "0" + s;
          if(t > 0){
            var curtime = '离活动结束: '+h + '时' + m + '分' + s + '秒';
          }else{
            var curtime = '活动已结束00时00分00秒';
          }
          times[i].curtime = curtime;
        }
        // var t = --time;
        // var h = Math.floor(t / 60 / 60);
        // var m = Math.floor((t - h * 60 * 60) / 60);
        // var s = t % 60;
        // if (h < 10) h = "0" + h;
        // if (m < 10) m = "0" + m;
        // if (s < 10) s = "0" + s;
        // var curtime = '活动结束时间: ' + h + '时' + m + '分' + s + '秒';
        that.setData({
          t: t || null,
          multigroup: times
        })
      }, 1000)
    }
  }

})