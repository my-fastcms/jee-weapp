function formatTime(date) {
  var year = date.getFullYear()
  var month = date.getMonth() + 1
  var day = date.getDate()
  var hour = date.getHours()
  var minute = date.getMinutes()
  var second = date.getSeconds()
  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

function formatDate(date) {
  var year = date.getFullYear()
  var month = date.getMonth() + 1
  var day = date.getDate()

  return [year, month, day].map(formatNumber).join('/')
}


function formatNumber(n) {
  n = n.toString()
  return n[1] ? n : '0' + n
}

function _login(me, callback) {
  //说明微信session过期
  wx.login({
    sucess: function (res) { },
    fail: function (res) { me.toString(res); },
    complete: function (res) {
      if (!res.code) {
        wx.showModal({
          title: '登陆失败',
          content: res.errMsg,
        })
      } else {
        me.ajaxJson("/user/login", { code: res.code }, function (res) {
          var json = res.data;
          if (json.code != 200) {
            wx.showModal({ showCancel: false, title: '登陆失败', content: json.msg ? json.msg : "" });
          } else {
            console.log("=====sessionid:" + json.data.sessionId + ",openid:" + json.data.openid)
            //wx.setStorageSync('sessionid', json.data.sessionId);
            wx.setStorageSync('openid', json.data.openid);
            getUserInfo(me, callback);
          }
        })
      }
    }
  })
}

function login(me, callback) { //me == app
  wx.checkSession({
    success: function () {
      console.log("===========wxsession is online")
      getUserInfo(me, callback);
    },
    fail: function () {
      //说明微信session过期
      console.log("===========wxsession is offline")
      _login(me, callback);
    }
  })
}

function getUserInfo(me, callback) { //me == app
  wx.getUserInfo({
    //withCredentials:true,
    success: function (res) {
      var userInfo = res.userInfo;
      //对用户数据进行签名校验
      var req = {};
      req.rawData = res.rawData;
      req.signature = res.signature;
      req.encryptedData = res.encryptedData;
      req.iv = res.iv;
      req.openid = wx.getStorageSync("openid");
      me.ajaxJson("/user/check", req, function (rescheck) {
        var userRes = rescheck.data;
        if (userRes.state != "ok") {
          me.data.userInfo = null;
          wx.showModal({
            showCancel: false, title: '授权过期，请重新授权登录', content: userRes.msg ? userRes.msg : "", success: function (res) {
              if (res.confirm) {
                console.log("===============re login=====");
                _login(me, callback);
              }
            }
          });
        } else {
          me.toString(rescheck);
          me.data.userInfo = userInfo;
          //建立点步服务器session
          wx.setStorageSync('sessionid', userRes.data.sessionId);
          if (callback) callback(userRes.data.buyerRes);//登录成功回传buyerUser对象
        }
      });
    },
    fail: function (res) {
      console.log("=================getUserInfo.fail");
      me.toString(res);
    }
  })
}

module.exports = {
  formatTime: formatTime,
  login: login,
  getUserInfo: getUserInfo,
  formatDate: formatDate,
}