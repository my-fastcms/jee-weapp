//app.js
var md5 = require('utils/md5.js')
var util = require('utils/util.js')
const updateManager = wx.getUpdateManager()
App({
  data: {
    appId: null
  },

  getDomain: function () {
    return "https://api.dbumama.com";
    //return "http://test.dbumama.com";
    //return "http://33gkr7.natappfree.cc";
    //return "http://127.0.0.1:8080";
  },

  onLaunch: function () {
    wx.setStorageSync('needAuth', false);
    //调用API从本地缓存中获取数据
    let extConfig = wx.getExtConfigSync ? wx.getExtConfigSync() : {}
    var appId = extConfig["appid"] || "";
    console.log("================appId:" + appId);
    this.data.appId = appId;
    
    updateManager.onUpdateReady(function () {
      wx.showModal({
        title: '更新提示',
        content: '新版本已经准备好，是否重启应用？',
        success: function (res) {
          if (res.confirm) {
            // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
            updateManager.applyUpdate();
          }
        }
      })
    })

    this.ajaxJson("/getStyle", {}, function(res){
      var json = res.data;
      if(json !=null && json.code==200){
        var styleData = json.data || null;
        if(styleData == null) return;
        wx.setStorageSync('navbar_bgcolor', styleData.navbarBgcolor || null);
        wx.setStorageSync('other_bgcolor', styleData.otherBgcolor || null);

        wx.setStorageSync('show_compensate', styleData.showCompensate || null);
        wx.setStorageSync('show_delivery_time', styleData.showDeliveryTime || null);
        wx.setStorageSync('show_safeguard', styleData.showSafeguard || null);
        
        wx.setTabBarStyle({
          color: styleData.tabbarColor || null,
          selectedColor: styleData.tabbarSelectedColor || null,
          backgroundColor: styleData.tabbarBgColor || null,
          borderStyle: 'white'
        });
        
        var tabItems = styleData.tabbarItems || {};
        for(var i=0;i<tabItems.length;i++){
          var item = tabItems[i];
          if(item != null && item.tabbarIndex !=null){
            wx.setTabBarItem({
              index: item.tabbarIndex,
              text: item.tabbarTitle || null,
              iconPath: item.tabbarIconPath || null,
              selectedIconPath: item.tabbarSelectedIconpath || null
            })
          }
        }
      }
    })
  },

  onShow: function(){
    console.log("===================app onShow");
  },

  onError: function(msg){
    console.info("================msg:" + msg);
  },

  onPageNotFound: function(){
    wx.switchTab({
      url: '/pages/user/user',
    })
  },

  ajaxJson: function (url, paraMap, callback, failCallback){
    var me = this;
    var tokenMap = {};
    var timestamp = new Date().getTime();
    tokenMap.serverKey="2016";
    tokenMap.timestamp=timestamp;
    for(var key in paraMap){
      tokenMap[key]=paraMap[key];
    }
    var token = this.getToken(tokenMap, "bcttcwls789");
    paraMap.sign=token;
    paraMap.serverKey="2016";
    paraMap.timestamp=timestamp;
    // var paraMapStr = paraMap.toString();
    // var message = des.des("DBUMAMAAPI", paraMapStr, 1, 0);
    // message = des.stringToHex(message);
    console.log("============================appId:" + me.data.appId)
    wx.showLoading({title: '加载中'});
    wx.request({
      url: this.getDomain() + url,
      method: "POST",
      header: {
          'content-type': 'application/json',
          "appid": me.data.appId || "",
          "sessionid": wx.getStorageSync('sessionid') || "",
          "Cookie": "JSESSIONID=" + wx.getStorageSync('sessionid') || ""
      },
      data: paraMap,
      success: function (res) {
        console.log("invoke " +url+ " Interface===========")
        if (res.data.code && res.data.code == 1000) {
          //自身服务器session失效，不是微信服务器
          wx.showModal({
            title: '提示',
            content: '您还没有授权登录，请在个人中心点击授权登录',
            success: function () {
              console.log("=========need auth========");
              wx.setStorageSync('needAuth', true);
              wx.switchTab({
                url: '/pages/user/user',
              })
            }
          });
          return;
        }
        //砍价连接 授权后跳转到砍价
        if (res.data.code && res.data.code == 2000) {
          //自身服务器session失效，不是微信服务器
          console.info("==========app.js self server session is timeout !!! ");
          var bargainUserId = paraMap["bargainUserId"];
          var productId = paraMap["productId"];
         
          wx.redirectTo({
            url: '/pages/user/auth?url=' + url + '&bargainUserId=' + bargainUserId + '&productId=' + productId,
          })
          return;
        }
        
        if(res.data.code != 200 && url !="/user/check"){
          wx.showModal({
            title: '提示',
            content: res.data.msg ? res.data.msg : "500",
          })
        }else{
          if (callback) callback(res);
        }
      },
      fail: function (res) { 
        if (failCallback) failCallback();
      },
      complete: function (res) {
        wx.hideLoading();
      }
    })
  },
  onShow: function () {
      console.log('===========App Show');
  },
  onHide: function () {
      console.log('===========App Hide');
  },
  
  objKeySort: function (obj) {//obj对象key排序的函数
    var newkey = Object.keys(obj).sort();
    var newObj = {};
    for(var i = 0; i<newkey.length; i++) {
      newObj[newkey[i]] = obj[newkey[i]];
    }
    return newObj;
  }, 
  getToken: function (obj, serverSecret){//生成请求Token
    var token = "";
    var hasAppKey = false;
    var tokenParam = this.objKeySort(obj);
    for(var key in tokenParam){
      if(key != "sign"){
        token += key+tokenParam[key];
      }
      if (key == "serverKey") hasAppKey = true;
    }
    if (!hasAppKey) throw "serverKey not found";
    token += "serverSecret" + serverSecret;
    //return byte2hex and md5
    //console.log("============token:" + token)
    //utf-8
    token = this.Utf8Encode(token);
    return md5.hex_md5(token).toUpperCase();
  },
  toString: function(obj){
      for(var key in obj){
          console.log("key:" + key + ",value:" + obj[key]);
      }
  },
  Utf8Encode: function (str) {
    var utftext = "";
    for  (var  n  = 0;  n  <  str.length;  n++)  {
      var c = str.charCodeAt(n); if (c < 128) {
        utftext += String.fromCharCode(c);
      } else if((c > 127) && (c < 2048)){
        utftext += String.fromCharCode((c >> 6) | 192);
        utftext += String.fromCharCode((c & 63) | 128);
      }else{
        utftext+=String.fromCharCode((c >> 12) | 224);
        utftext+=String.fromCharCode(((c >> 6) & 63) | 128);
        utftext+=String.fromCharCode((c & 63) | 128);
      }
    }
   return utftext;  
  }  

})
