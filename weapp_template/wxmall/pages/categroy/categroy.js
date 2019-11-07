var app = getApp();
Page({
  data: {
    categorys: []
  },
  onLoad: function () {
    
    var that = this
    app.ajaxJson('/category/list', {}, function (res) {
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      that.setData({
        categorys: json.data
      });
     
    });

  },
  bindViewTap: function (e) {
      var param = e.currentTarget.dataset;
      var id = param["id"];
      var str=''
      if(id){
        str = '?id=' + id;
      }
      
      //使用redirectTo
      wx.navigateTo({  //如果使用，navigateTo，购买-地址页面就超过5层
          url: '../product/list/list'+str
      })
  }

})