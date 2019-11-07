//index.js
//获取应用实例
var app = getApp()
Page({
  data: {
    activeCategoryId: 0,
    hotCount: 0,
    isHidden: false,
    page: 1,
    products: [],
    imageList: [],
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor')
  },

  onLoad: function () {
    var that = this;
    wx.getSystemInfo({
      success: function (res) {
        var scollHeight = res.screenHeight - 90;
        that.setData({
          scollHeight: scollHeight
        });
      }
    });
    that.getIndexData();
    that.getProducts(that.data.page);
    //that.getSeckill(that.data.page);
    //that.getMultiGroup(that.data.page);
    //that.getBargainProduct(that.data.page);
    //that.getPromotion(that.data.page);
    //that.getFullcut(that.data.page);
    //that.getAgentProduct(that.data.page);
    that.getImages();
  },
  getImages: function () {
    // 获取图片
    var that = this;
    app.ajaxJson('/sowingimage', {}, function (res) {
      var images = res.data.data;// 获取所有图片的集合
      var imageList = [];
      for (var i = 0; i < images.length; i++) {
        var img = new Object();
        img.src = images[i].sowingImg;
        img.url = images[i].sowingUrl;
        imageList.push(img);
      }
      that.setData({
        imageList: imageList
      });
    });
  },
  getIndexData: function () {
    var that = this;
    app.ajaxJson('/index', {}, function (res) {
      var json = res.data;
      wx.setStorageSync('authUser', json.data.authUser);

      // var hotCount = 0;
      // if (json.data.hotProducts) {
      //   hotCount = json.data.hotProducts.lentgh;
      // }
      var newCount = 0;
      if (json.data.newProducts) {
        newCount = json.data.newProducts.length;
      }

      // var indexCount = 0;
      // if (json.data.newProducts) {
      //   indexCount = json.data.indexProducts.length;
      // }
      // var commondCount = 0;
      // if (json.data.commondProducts) {
      //   commondCount = json.data.commondProducts.length;
      // }

      that.setData({
        shop: json.data.shop || {},
        //hotProducts: json.data.hotProducts,
        //hotCount: hotCount,
        newProducts: json.data.newProducts,
        newCount: newCount,
        //indexProducts: json.data.indexProducts,
        //indexCount: indexCount,
        //commondProducts: json.data.commondProducts,
        //commondCount: commondCount,
        //authUser: json.data.authUser,
        categories: json.data.categories
      });
    });
  },

  getProducts: function (page) {
    if (this.data.endFlag) {
      return;
    }
    var that = this;
    that.data.page = page;
    var req = {}
    req.page = page;
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
      if (json.data && json.data.length > 0) {
        that.setData({
          products: json.data
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

  //获取秒杀商品
  getSeckill: function () {
    var that = this;
    app.ajaxJson('/seckill/list', {}, function (res) {
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      if (json.data && json.data.length > 0) {
        that.setData({
          seckill: json.data
        });
      }
    });
  },

  //获取人气拼团商品
  getMultiGroup: function () {
    var that = this;
    app.ajaxJson('/group/list', {}, function (res) {
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      if (json.data && json.data.length > 0) {
        that.setData({
          multigroup: json.data
        });
      }
    });
  },
  //获取砍价商品
  getBargainProduct: function () {
    var that = this;
    app.ajaxJson('/bargain/list', {}, function (res) {
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      if (json.data && json.data.length > 0) {
        that.setData({
          bargains: json.data
        });
      }
    });
  },
  //获取限时打折商品
  getPromotion: function () {
    var that = this;
    app.ajaxJson('/promotion/list', {}, function (res) {
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      if (json.data && json.data.length > 0) {
        that.setData({
          promotion: json.data
        });
      }
    });
  },
  //获取分销商品
  getAgentProduct: function () {
    var that = this;
    app.ajaxJson('/agent/list', {}, function (res) {
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      if (json.data && json.data.length > 0) {
        that.setData({
          agentProduct: json.data
        });
      }
    });
  },
  //获取满减送商品
  getFullcut: function () {
    var that = this;
    app.ajaxJson('/fullcut/list', {}, function (res) {
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      if (json.data && json.data.length > 0) {
        that.setData({
          fullcut: json.data
        });
      }
    });
  },

  toDetails: function (e) {
    var param = e.currentTarget.dataset;
    var id = param["id"];
    wx.navigateTo({
      url: '../product/detail/detail?id=' + id
    })
  },
  
  toSeckill: function (e) {
    var param = e.currentTarget.dataset;
    var id = param["id"];
    wx.navigateTo({
      url: '../seckill/detail/detail?id=' + id
    })
  },

  //砍价页面
  toBargainList: function (e) {
    var param = e.currentTarget.dataset;
    var id = param["id"];
    wx.navigateTo({
      url: '../bargain/detail?id=' + id
    })
  },
  getPromtions: function (e) {
    var param = e.currentTarget.dataset;
    var _type = param["type"];
    wx.navigateTo({
      url: '../product/list/list?type=' + _type,
    })
  },
  tabClick: function (e) {
    var me = this;
    this.setData({
      activeCategoryId: e.currentTarget.id
    });
    if (e.currentTarget.id == 0) {
      me.setData({
        isHidden: false,
        endFlag: false,
        products: []
      });
    } else {
      console.info("===========cateId:" + e.currentTarget.id);
      me.setData({
        isHidden: true,
        endFlag: false,
        products: []
      });
    }
    me.getProducts(1);
  },
  lunboClick: function (e) {
    // console.log("id:" + e.currentTarget.id);
    wx.switchTab({
      url: '/pages/user/user',
    })
  },
  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function (res) {
    var me = this;
    if (res.from === 'button') {
      // 来自页面内转发按钮
      console.log(res.target)
    }
    return {
      title: '推荐你看看，精选好货限时打折可拼团',
      path: '/pages/index/index',
      success: function (res) {
        // 转发成功
      },
      fail: function (res) {
        // 转发失败
      }
    }
  },
  lunboJump: function (event) {
    // wxml通过 data-* 传递数据，可带参数过来
    // js通过 event.currentTarget.dataset.* 获取wxml的参数
    var that = this;
    var url = event.currentTarget.dataset.url;
    
    if(url === "pages/cart/cart" || 
        url === "pages/user/user" ||
        url === "pages/index/index"){
      wx.switchTab({
        url: '../../' + url
      });
      return;
    }
    
    wx.navigateTo({
      url: '../../' + url
    })
  }
  // onPullDownRefresh: function () {
  //   this.getIndexData();
  //   var pageNo = this.data.page + 1;
  //   console.log("====pageNo:" + pageNo);
  //   this.getProducts(pageNo);
  //   wx.stopPullDownRefresh();
  // },
  // onReachBottom: function(){
  //   if (!this.data.endFlag && !this.data.loadingFlag) {
  //     var page = this.data.page + 1;
  //     console.info("===========加载第[" + page + "]页数据");
  //     this.getProducts(page);
  //   }else{
  //     console.info("没有数据了...");
  //   } 
  // }
})
