//index.js
//获取应用实例
var app = getApp()
Page({
  data: {
    addressList: {}
  },
  selectShop: function (e) {
    if (this.data.fromPage == 'user') {
      return;
    }
    var shopId = e.currentTarget.id;
    for (var i = 0; i < this.data.addressList.length; i++) {
      var r = this.data.addressList[i];
      if (shopId && r.id == shopId) {
        app.data.shop = r;
        console.log(r);
        break;
      }
    }
    wx.navigateBack();
  },

  onLoad: function (param) {
    var shopId = null;
    var fromPage = "order"
    if (param) {
      this.data.shopId = param.id;
      if (param.fromPage == 'user') {
        this.data.fromPage == 'user'
      } else {
        this.data.fromPage = fromPage;
      }
    } else {
      this.data.shopId = null;
      this.data.fromPage = fromPage;
    }

  },

  onShow: function () {
    var that = this;
    app.ajaxJson('/receiver/getShop', {}, function (res) {
      console.log(res);
      var json = res.data.data;
      if (that.data.shopId) {
        var shopId = parseInt(that.data.shopId);
        for (var i = 0; i < json.length; i++) {
          var r = json[i];
          if (shopId && r.id == shopId) {
            r.isSelected = true;
          } else {
            r.isSelected = false;
          }
        }
      } 
      that.setData({
        addressList: json
      });
    })
  }

})
