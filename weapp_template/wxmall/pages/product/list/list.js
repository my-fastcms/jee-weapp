var app = getApp();
Page({
  data: {
      inputShowed: false, 
      inputVal: "",//search value
      products:[],
      param:{},
      page:1,
      endFlag:false,
      loadingFlag:false,
      categories:null,
      activeCategoryId: 0,
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor')
  },
  onLoad: function (cate) {
    var that = this;
    wx.getSystemInfo({
      success: function (res) {
          //设置scroll-view 的高度
          var scollHeight = res.screenHeight - 90-65; 
          that.setData({
              scollHeight:scollHeight,
              products:[]
            }
          );
          if (cate && cate.id){
            that.data.param.categId = parseInt(cate.id);
          }
          that.getCategroys();
          that.getProducts(that.data.page);
      }
    })      
  },

  getCategroys: function () {
    var that = this;
    app.ajaxJson('/category/list', {}, function (res) {
      var json = res.data;
      if (json.code == 200) {
        that.setData({
          categories: json.data
        });
      }
    });
  },

  tabClick: function (e) {
    this.setData({
      activeCategoryId: e.currentTarget.id
    });
    this.setData({
      products: [],
      endFlag: false
    });
    this.getProducts(1);
  },

  getProducts:function(page){
    if(this.data.endFlag){
      return;
    }
    var that = this;
    that.data.page=page;
    var req={}
    req.page=page;
    req.keyword = that.data.inputVal;
    req.categId = that.data.activeCategoryId != 0 ? that.data.activeCategoryId : null;  
    that.data.loadingFlag = true;
    app.ajaxJson('/product/list', req, function (res) {
      that.data.loadingFlag = false;
      var json = res.data;
      if (json.code != 200) {
        that.setData({
          endFlag: true
        });
        return;
      }
      if(json.data&& json.data.length>0){
        that.setData({
          products: that.data.products.concat(json.data)
        });
      }else {
        that.setData({
          endFlag:true
        });
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
       this.getProducts(page);
    } 
    // else {
    //   wx.showToast({
    //     title: '已到最后'
    //   })
    // }
  },

  onReachBottom: function () {
    console.info("====================上拉加载 endFlag:" + this.data.endFlag)
    this.lower();
  },

  //事件处理函数
  bindViewTap: function(e) {
      var param = e.currentTarget.dataset;
      var id = param["id"];
      wx.navigateTo({
          url: '../detail/detail?id='+id
      })
  },

  //search-----
  search: function () {
    this.setData({
      products: [],
      page: 1,
      endFlag: false,
      loadingFlag: false,
      inputShowed: true
    });
    this.getProducts(1);
  },
  resetSearch: function () {
    this.setData({
      inputVal: "",
      inputShowed: false,
      products: [],
      page: 1,
      endFlag: false,
      loadingFlag: false,
    });
    this.getProducts(1);
  },
  clearInput: function () {
    this.setData({
      inputVal: ""
    });
  },
  inputTyping: function (e) {
    this.setData({
      inputVal: e.detail.value
    });
  }
})