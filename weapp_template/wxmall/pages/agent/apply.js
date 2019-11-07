//index.js
//获取应用实例
var app = getApp()
Page({
  data: {
    imgcodeSrc: "",
    agent:null,
    mobile:"",
    name:null,
    phone:null,
    verifyCodeTime : "获取验证码",
    buttonDisable:false,
    webctx: app.getDomain(),
    address:"请选择地址"
  },
  onLoad: function () {
    var that = this;
    //检查是否有代理信息
    that.getImgCode();
  },
  inputWacth: function (e) {
    console.log(e);
    let item = e.currentTarget.dataset.model;
    this.setData({
      [item]: e.detail.value
    });
  },
  onShow: function(e){
    var me = this;
    app.ajaxJson("/agent/getAgent", {}, function (res) {
      var json = res.data;
      if (json.code == 200) {
        me.setData({
          agent: json.data || null
        });
      }
    });
  },
  getName: function (e) {
    var val = e.detail.value;
    this.setData({
      name: val
    });
  },
  getPhone: function (e) {
    var val = e.detail.value;
    this.setData({
      phone: val
    });
  },
  selectAddr: function (e) {
    var that = this;
    wx.chooseLocation({
      success: function (res) {
        that.setData({
          address: res.address
        })
      }
    })
  },
  sendCode: function(e){
    var that = this;
    if (this.data.buttonDisable) return false;
    var mobile = this.data.mobile;
    if(mobile == "" || mobile==null){
      wx.showToast({
        title: '请输入手机号码',
      })
      return false;
    }
    var regMobile = /^1\d{10}$/;
    if (!regMobile.test(mobile)) {
      wx.showToast({
        title: '手机号有误！'
      })
      return false;
    }
    var c = 60;
    var intervalId = setInterval(function () {
      c = c - 1;
      that.setData({
        verifyCodeTime: c + 's后重发',
        buttonDisable: true
      })
      if (c == 0) {
        clearInterval(intervalId);
        that.setData({
          verifyCodeTime: '获取验证码',
          buttonDisable: false
        })
      }
    }, 1000)
    app.ajaxJson("/sendCode", { phone:mobile}, function(res){
      var json = res.data;
      if(json.code !=200){
        wx.showModal({
          title: '错误',
          content: json.msg == null ? "发送短信失败" : "发送短信失败:"+json.msg,
        })
        return;
      }else {
        wx.showToast({
          title: '短信已发送',
        })
      }
    });
  },
  saveForm: function (e) {
    var that = this;
    var name = e.detail.value.name;
    var mobile = e.detail.value.mobile;
    var phoneCode = e.detail.value.phoneCode;
    var imgCode = e.detail.value.imgCode;
    var address = this.data.address;
    var parentId = e.detail.value.parentId || null;
    
    if (name == "") {
      wx.showToast({
        title: '请填写联系人',
      });
      return false;
    }
    if (mobile == "") {
      wx.showToast({
        title: '请填写手机号码'
      })
      return false;
    }
    if (phoneCode == "") {
      wx.showToast({
        title: '请输入短信验证码'
      })
      return false;
    }
    if (imgCode == "") {
      wx.showToast({
        title: '请输入图片验证码'
      })
      return false;
    }
    if (address == "" || address=="请选择地址") {
      wx.showToast({
        title: '请填写详细地址'
      })
      return false;
    }

    wx.showModal({
      title: '申请提示',
      content: '确认提交申请吗?',
      success: function (res) {
        if (res.confirm) {
          app.ajaxJson('/agent/save', {
            addr: address,
            agentName: name,
            parentId: parentId,
            agentPhone: mobile,
            phoneCode: phoneCode,
            imgcode: imgCode
          }, function (res) {
            var json = res.data;
            if (json.code != 200) {
              wx.showModal({
                title: '错误',
                content: json.msg
              })
              return false;
            }
            wx.showToast({
              title: '提交成功',
              success:function(){
                wx.switchTab({
                  url: '../user/user',
                })
              }
            })
          });
        } 
      }
    })
  },
  getImgCode: function(){
    var me = this;
    app.ajaxJson("/captcha", {}, function(res){
      var json = res.data;
      if(json.code == 200){
        me.setData({
          imgcodeSrc: json.data
        });
      }
    });
  }
})