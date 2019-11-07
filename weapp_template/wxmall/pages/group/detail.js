// pages/group/info.js
var app = getApp();
Page({
  data:{
    num:1,
    specificationArray: [],
    attrValueList: [],
    groupInfo : null
  },

  onLoad:function(params){
    var groupId = params.groupId;
    console.log("==================groupId:" + groupId);
    this.getDetail(groupId);
  },

  getDetail: function (id) {
    var that = this;
    app.ajaxJson('/group/detail', {groupId : id}, function (res) {
      var json = res.data;
      if (json.code == 200) {
        var detail = json.data;
        that.setData({
          groupInfo: detail,
          specPriceMap: detail.priceMap
        });
        console.info(that.data.groupInfo.expiresIn);
        that.setTimeData(that.data.groupInfo.expiresIn);

        that.distachAttrValue(detail);
        //默认单规格情况下
        if (!detail.specifications || detail.specifications.length == 0) {
          var price = parseFloat(detail.multiGroupInfo.collagePrice);
          price = price.toFixed(2);
          var totalPrice = price * that.data.num;
          totalPrice = totalPrice.toFixed(2);
          that.setData({
            price: price,
            totalPrice: totalPrice
          });
        }
      }
    });
  },

  onShow: function () {
    
  },
  
  setTimeData:function(time){
    var self = this;
    setInterval(function(){
        var t = --time;
        var h =  Math.floor(t/60/60);
        var m = Math.floor((t-h*60*60)/60);
        var s = t%60;
        if(h<10 && h>0) h = "0"+h;
        if(m<10 && m>0) m = "0"+m;
        if(s<10 && s>0) s = "0"+s;
        var timeStr = h+':'+m+':'+s
        
      self.setData({
        leftTime:timeStr
      })
    }, 1000)
  },

  goToHome:function(){
    wx.switchTab({
      url:'/pages/index/index'
    })
  },

  setModalStatus: function (e) {
    var that = this;
    //清除已选择规格
    var attrValues = this.data.attrValueList;
    for (var k = 0; k < attrValues.length; k++) {
      if (attrValues[k].selectedValue && attrValues[k].selectedValue != null) {
        attrValues[k].selectedValue = null;
      }
    }

    this.setData({
      attrValueList: attrValues
    });

    var status = e.currentTarget.dataset.status;
    var animation = wx.createAnimation({
      duration: 200,
      timingFunction: "linear",
      delay: 0
    })
    this.animation = animation
    animation.translateY(300).step()
    this.setData({
      animationData: animation.export()
    })
    if (status == 1) {
      this.setData({
        showModalStatus: true
      });
    }

    var price = 0.00;

    //拼团情况显示拼团价格（单规格的情况，多规格另外处理）
    if (that.data.groupInfo.specifications == null || that.data.groupInfo.specifications.length <= 0) {
      price = parseFloat(that.data.groupInfo.multiGroupInfo.collagePrice);
    }

    price = price.toFixed(2);
    var totalPrice = price * that.data.num;
    totalPrice = totalPrice.toFixed(2);
    that.setData({
      price: price,
      totalPrice: totalPrice
    });

    setTimeout(function () {
      animation.translateY(0).step();
      this.setData({
        animationData: animation
      })
      if (status == 0) {
        this.setData({
          showModalStatus: false,
          num: 1
        });
      }
    }.bind(this), 100)
  },

  pay: function (e) {
    var that = this;
    var sflag = true;
    for (var i = 0; i < that.data.specificationArray.length; i++) {
      if (that.data.specificationArray[i].sfId == '') {
        sflag = false;
        break;
      }
    }
    if (!sflag) {
      wx.showToast({
        title: '请选择规格...',
        icon: 'success',
        duration: 1000
      })
      return;
    }
    var quantity = that.data.num;
    var speci = JSON.stringify(that.data.specificationArray);
    var productId = that.data.groupInfo.productId;
    var itemsArray = new Array();
    var entity = new Object();
    entity.productId = productId;
    entity.pcount = quantity;
    entity.speci = speci;
    itemsArray.push(entity);
    console.log(JSON.stringify(itemsArray));
    wx.navigateTo({
      url: '../pay/jiesuan?flag=3&groupId='+that.data.groupInfo.groupId+'&items=' + JSON.stringify(itemsArray)
    })
    this.setData({
      showModalStatus: false
    });
  },

  bindMinus: function (e) {
    var num = this.data.num;
    // 如果只有1件了，就不允许再减了
    if (num > 1) {
      num--;
      this.setTotalPrice(num);
    }
  },

  bindPlus: function (e) {
    var num = this.data.num;
    if (num >= 99999) {
      return;
    }
    // 自增
    num++;
    this.setTotalPrice(num);
  },

  bindManual: function (e) {
    var num = parseInt(e.detail.value);
    if (isNaN(num)) {
      num = 1;
    }
    // 将数值与状态写回
    if (num > 0 && num < 100000) {
      this.setTotalPrice(num);
    }
  },

  setTotalPrice: function (num) {
    var totalPrice = '';
    if (!isNaN(this.data.price)) {
      totalPrice = this.data.price * num;
      totalPrice = totalPrice.toFixed(2);
    }
    this.setData({
      num: num,
      totalPrice: totalPrice
    });
  },

  bindManualTapped: function () {
    // 什么都不做，只为打断跳转
  },

  distachAttrValue: function (detail) {
    var attrValueList = this.data.attrValueList;
    var specificationArray = this.data.specificationArray;
    if (detail.specifications && detail.specifications.length > 0) {
      for (var i = 0; i < detail.specifications.length; i++) {
        var attrKey = "";
        var attrKeyId = "";
        var attrValues = [];
        var attrValuesId = [];
        var attrValueStatus = [];
        attrKey = detail.specifications[i].specification.name;
        attrKeyId = detail.specifications[i].specification.id;
        specificationArray.push({
          sfId: '',
          spvId: ''
        });
        for (var j = 0; j < detail.specifications[i].specificationValues.length; j++) {
          attrValues.push(detail.specifications[i].specificationValues[j].name);
          attrValuesId.push(detail.specifications[i].specificationValues[j].id);
          attrValueStatus.push(true);
        }
        attrValueList.push({
          attrKey: attrKey,
          attrKeyId: attrKeyId,
          attrValues: attrValues,
          attrValuesId: attrValuesId,
          attrValueStatus: attrValueStatus
        });
      }
    }

    this.setData({
      attrValueList: attrValueList
    });
  },

  selectAttrValue: function (e) {
    var me = this;
    var attrValueList = this.data.attrValueList;
    var specificationArray = this.data.specificationArray;
    var index = e.currentTarget.dataset.index;//属性索引
    var key = e.currentTarget.dataset.key;
    var value = e.currentTarget.dataset.value;
    var sfId = e.currentTarget.dataset.sfid;
    var spvId = e.currentTarget.dataset.id;
    if (e.currentTarget.dataset.status) {
      specificationArray[index].sfId = sfId;
      specificationArray[index].spvId = spvId;
      var spcs = [];
      for (var p = 0; p < specificationArray.length; p++) {
        spcs.push(specificationArray[p].spvId);
      }
      var spckey = spcs.join(",");
      var spec = this.data.specPriceMap[spckey];
      var price = 0;
      var totalPrice = '';
      if (spec) {
        price = spec.collagePrice;
        totalPrice = price * this.data.num;
      }
      attrValueList[index].selectedValue = value;
      this.setData({
        price: price,
        totalPrice: totalPrice,
        attrValueList: attrValueList
      });
    }
  },

  showGoodsDetail: function(e){
    var id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/product/detail/detail?id=' + id,
    })
  },

  onShareAppMessage: function (options) {
    console.log(options)
    var me = this;
    var path = '/pages/product/detail/detail?id=' + this.data.groupInfo.productId
    return {
      title: me.data.groupInfo.productName,
      path: path,
      success: function (res) {
        console.log(path)
        console.log(res)
      }
    }
  }

})