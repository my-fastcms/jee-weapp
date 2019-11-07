var app = getApp();
Page({
  data: {
    carts: [],
    fullCuts: [],
    minusStatuses: [],
    selectedAllStatus: true,
    total: "",
    fullCutInfo: "",
    oldTotal: "",
    navbar_bgcolor: wx.getStorageSync('navbar_bgcolor'),
    other_bgcolor: wx.getStorageSync('other_bgcolor'),
    cashValue: 0 //满减值
  },

  onLoad: function() {
    
  },

  onShow: function() {
    var that = this
    app.ajaxJson('/cart/list', {}, function(res) {
      var json = res.data;
      if (json.code != 200) {
        return;
      }
      that.setData({
        carts: json.data.cartItems,
        fullCuts: json.data.fullCuts
      });
      that.sum();
    });
  },

  bindMinus: function(e) {
    var index = parseInt(e.currentTarget.dataset.index);
    var num = this.data.carts[index].quantity;
    // 如果只有1件了，就不允许再减了
    if (num > 1) {
      num--;
    }
    // 只有大于一件的时候，才能normal状态，否则disable状态
    var minusStatus = num <= 1 ? 'disabled' : 'normal';
    // 购物车数据
    var carts = this.data.carts;
    carts[index].quantity = num;
    // 按钮可用状态
    var minusStatuses = this.data.minusStatuses;
    minusStatuses[index] = minusStatus;
    // 将数值与状态写回
    this.setData({
      carts: carts,
      minusStatuses: minusStatuses
    });

    this.sum();
  },
  bindPlus: function(e) {
    var index = parseInt(e.currentTarget.dataset.index);
    var num = this.data.carts[index].quantity;
    // 自增
    if (num < 99999) {
      num++;
    }
    // 只有大于一件的时候，才能normal状态，否则disable状态
    var minusStatus = num >= 99999 ? 'disabled' : 'normal';
    // 购物车数据
    var carts = this.data.carts;
    carts[index].quantity = num;
    // 按钮可用状态
    var minusStatuses = this.data.minusStatuses;
    minusStatuses[index] = minusStatus;
    // 将数值与状态写回
    this.setData({
      carts: carts,
      minusStatuses: minusStatuses
    });

    this.sum();
  },
  bindManual: function(e) {
    var index = parseInt(e.currentTarget.dataset.index);
    var carts = this.data.carts;
    var num = parseInt(e.detail.value);
    if (isNaN(num)) {
      num = 1;
    }
    if (num < 1) {
      num = 1
    }
    if (num > 99999) {
      num = 99999;
    }
    carts[index].quantity = num;
    // 将数值与状态写回
    this.setData({
      carts: carts
    });
    this.sum();
  },
  bindManualTapped: function() {
    // 什么都不做，只为打断跳转
  },
  bindCheckbox: function(e) {
    /*绑定点击事件，将checkbox样式改变为选中与非选中*/
    //拿到下标值，以在carts作遍历指示用
    var index = parseInt(e.currentTarget.dataset.index);
    //原始的icon状态
    var selected = this.data.carts[index].selected;
    var carts = this.data.carts;
    // 对勾选状态取反
    carts[index].selected = !selected;
    // 写回经点击修改后的数组
    this.setData({
      carts: carts,
    });

    this.sum();
  },
  bindSelectAll: function() {
    // 环境中目前已选状态
    var selectedAllStatus = this.data.selectedAllStatus;
    // 取反操作
    selectedAllStatus = !selectedAllStatus;
    // 购物车数据，关键是处理selected值
    var carts = this.data.carts;
    // 遍历
    for (var i = 0; i < carts.length; i++) {
      carts[i].selected = selectedAllStatus;
    }

    this.setData({
      selectedAllStatus: selectedAllStatus,
      carts: carts,
    });
    this.sum();
  },
  bindCheckout: function() {
    if (this.data.carts.length == 0) {
      wx.showToast({
        title: '购物车没有商品！',
        //icon: 'loading',
        duration: 1000
      });
      return;
    }
    var items = this.itemsArray();
    if (items.length == 0) {
      wx.showToast({
        title: '请勾选商品！',
        icon: 'success',
        duration: 1000
      });
      return;
    }
    var itemStr = JSON.stringify(items);

    wx.navigateTo({
      url: '../pay/jiesuan?items=' + itemStr
    });
    
  },

  delete: function(e) {
    var that = this;
    // 购物车单个删除
    var objectId = e.currentTarget.dataset.objectId;
    console.log(objectId);
    wx.showModal({
      title: '提示',
      content: '确认要删除吗',
      success: function(res) {
        if (res.confirm) {
          app.ajaxJson('/cart/delete', {
            ids: encodeURIComponent(objectId)
          }, function(res) {
            var json = res.data;
            if (json.code != 200) {
              wx.showModal({
                showCancel: false,
                title: '错误',
                content: json.msg ? json.msg : "500",
              })
              return;
            } else {
              wx.showToast({
                title: '删除成功',
                icon: 'success',
                duration: 1000
              });
              that.onShow();
            }
          });

        }
      }
    })

  },
  calcIds: function() {
    // 遍历取出已勾选的cid
    // var buys = [];
    var cartIds = [];
    for (var i = 0; i < this.data.carts.length; i++) {
      if (this.data.carts[i].selected) {
        // 移动到Buy对象里去
        // cartIds += ',';
        cartIds.push(this.data.carts[i].goodId);
      }
    }
    if (cartIds.length <= 0) {
      wx.showToast({
        title: '请勾选商品',
        icon: 'success',
        duration: 1000
      })
    }
    return cartIds;
  },
  itemsArray: function() {
    var that = this;
    var itemsArray = [];
    for (var i = 0; i < that.data.carts.length; i++) {
      if (that.data.carts[i].selected) {
        var entity = new Object();
        entity.productId = that.data.carts[i].goodId;
        entity.pcount = that.data.carts[i].quantity;
        var speciArray = new Array();
        var specificationValues = that.data.carts[i].specificationValues;
        for (var j = 0; j < specificationValues.length; j++) {
          var speciEntity = new Object();
          speciEntity.spvId = specificationValues[j].id;
          speciArray.push(speciEntity);
        }
        if (speciArray.length > 0) {
          entity.speci = JSON.stringify(speciArray);
        }
        itemsArray.push(entity);
      }
    }

    return itemsArray;
  },
  sum: function() {
    var carts = this.data.carts;
    // 计算总金额
    //console.log(carts)
    var total = 0;
    for (var i = 0; i < carts.length; i++) {
      if (carts[i].selected) {
        total += carts[i].quantity * carts[i].goodPrice;
      }
    }
    // .
    //total = total.toFixed(2);

    // .
    var oldTotal = total.toFixed(2);

    //计算满减的情况
    var fullCuts = this.data.fullCuts;

    // .
    //var oldTotal = "";

    // .
    var mcash = 0;

    var fullCutInfo = "";

    for (var i = 0; i < fullCuts.length; i++) {

      // . for & if
      for (var j = 0; j < carts.length; j++) {
        if (carts[j].selected && carts[j].fullCutResultDtos != null && carts[j].fullCutResultDtos.length > 0) {
          
          var fullCut = fullCuts[i];
          if (total >= fullCut.meet) {
            //符合满减条件
            var cash = fullCut.cash;
            if (cash != null && cash != "") {

              // .
              if (cash >= mcash) { // 先找出最大的满减金额
                mcash = cash;
                fullCutInfo = "满" + fullCut.meet + "减" + mcash;
              }

              // .
              //oldTotal = total;
              //fullCutInfo = "满" + fullCut.meet + "减" + cash;
              //total = total - cash;
            }
            var postage = fullCut.postage;
            if (postage == 1) {
              fullCutInfo = "满" + fullCut.meet + "包邮";
            }
          }

        }
      }
    }

    // .
    var total = total - mcash;
    total = total.toFixed(2);
    parseFloat(oldTotal) - parseFloat(total) > 0 ? oldTotal = oldTotal : oldTotal = '';

    // 写回经点击修改后的数组
    this.setData({
      carts: carts,
      total: total,
      oldTotal: oldTotal,
      fullCutInfo: fullCutInfo
    });
    //this.hiddenText(oldTotal+'');
  },
  showGoods: function(e) {
    var param = e.currentTarget.dataset;
    var id = param["objectId"];
    wx.navigateTo({
      url: '../product/detail/detail?id=' + id
    });
  },
  onPullDownRefresh: function() {
    this.onShow();
    wx.stopPullDownRefresh();
  },
  hiddenText: function(str) {
     if (str.length > 7) {
       this.setData({
         oldTotal: ''
       })
     } 
   }

})