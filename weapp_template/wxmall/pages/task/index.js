var app = getApp();
Page({
  data: {
    param: {},
    formIds: [],
  },
  onLoad: function (cate) {
    var that = this;
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
        that.getTask();
      }
    })
  },
  getTask: function () {
    var that = this;
    var req = {}
    app.ajaxJson('/task/online', req, function (res) {
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      if (json.data) {
        that.setData({
          taskList: json.data
        });
      }
    });
  },
  makeTask: function (e) {
    if (e.detail.errMsg == "getUserInfo:ok"){
      var params = {}
      params.nickName = e.detail.userInfo.nickName
      params.avatarUrl = e.detail.userInfo.avatarUrl
      params.formIds = JSON.stringify(this.data.formIds)
      console.log("===============formId:" + params.formIds);
      app.ajaxJson('/task/make', params, function (res) {
        var json = res.data;
        if (json.code != 200) {
          return;
        }
        if (json.data && json.data.length > 0) {
          wx.previewImage({
            urls: [json.data]
          })
        }
      });
    }
  },
  formSubmit: function (e) {
    var formId = e.detail.formId || null;//用于模板消息推送
    this.data.formIds.push(formId)
   
  },
  goTaskList: function () {
    wx.navigateTo({
      url: '/pages/task/list',
    })
  },
})
