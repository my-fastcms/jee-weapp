var app = getApp();
Page({
  data: {
    param: {},
    taskName: '',
    userName: '',
    openId: '',
  },
  onShow: function (){
    var that = this
    wx.getSetting({
      success: function(res){
        if (res.authSetting['scope.userInfo']){
          app.ajaxJson('/task/scan', { openId: that.data.openId }, function (res) {
            var json = res.data;
              if (json.code == 200) {
              }
           })
          }
      }
    })
  },
  onLoad: function (cate) {
    var that = this;
    const scene = decodeURIComponent(cate.scene);
    that.data.openId = scene;
    that.getOnlineTask(scene);
    wx.getSystemInfo({
      success: function (res) {
        //设置scroll-view 的高度
        var scollHeight = res.screenHeight - 90 - 65;
        that.setData({
          scollHeight: scollHeight,
        }
        );
        if (cate && cate.id) {
          that.data.param.categId = parseInt(cate.id);
        }
      }
    })
  },
  goTaskIndex: function(){
    wx.switchTab({
      url: '/pages/index/index',
    })
  },
  getOnlineTask: function (scene) {
    var that = this;
    app.ajaxJson('/task/online', {scene: scene}, function (res) {
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      if (json.data) {
        that.setData({
          taskName: json.data.taskName,
          userName: json.data.userName
        });
      } 
    });
  },
})