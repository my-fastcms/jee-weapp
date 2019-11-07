//index.js
//获取应用实例
var app = getApp()
Page({
  data: {
    addressList: {},
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor')
  },
  addAddess: function () {
      wx.redirectTo({
        url: "/pages/address/addloction"
      })
  },
  selectAddress: function (e) {
    if (this.data.fromPage == 'user') {
      return;
    }
    var selectId = e.currentTarget.id;
    for (var i = 0; i < this.data.addressList.length; i++) {
      var r = this.data.addressList[i];
      if (selectId && r.id == selectId) {
        app.data.receiver = r;
        break;
      }
    }
    wx.navigateBack();
  },

  onLoad: function (param) {
    var selectId = null;
    var fromPage = "order"
    if (param) {
      this.data.selectId = param.id;
      if (param.fromPage == 'user') {
        this.data.fromPage == 'user'
      } else {
        this.data.fromPage = fromPage;
      }
    } else {
      this.data.selectId = null;
      this.data.fromPage = fromPage;
    }

  },
  onShow: function () {
    var that = this;
    app.ajaxJson('/receiver/list', {}, function (res) {
      var json = res.data.data;
      if (that.data.selectId) {
        var selectId = parseInt(that.data.selectId);
        for (var i = 0; i < json.length; i++) {
          var r = json[i];
          if (selectId && r.id == selectId) {
            r.isSelected = true;
          } else {
            r.isSelected = false;
          }
        }
      } else {
        for (var i = 0; i < json.length; i++) {
          var r = json[i];
          r.isSelected = r.is_default;
        }
      }
      that.setData({
        addressList: json
      });
    })
  }

})
