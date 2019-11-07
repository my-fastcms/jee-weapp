
const app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    tabs: [ "一级","二级"],
    teams:[],
    activeIndex: 0,
  },
  tabClick: function(e){
    
    this.setData({
      teams: [],
      index: 1,
      activeIndex: 1,

    });
    this.getTeams(this.data.page);
  },
  tabClickTo: function (e) {
    var activeIndex = e.currentTarget.id;
    this.setData({
      teams: [],
      index: activeIndex,
      activeIndexTo: activeIndex,
    });
    this.getTeams(activeIndex);
  },
  
  /**
   * 生命周期函数--监听页面加载
   */
  
  onLoad: function (options) {
    var that = this;
    wx.getSystemInfo({
      success: function(res) {
        var screenHeight = res.screenHeight - 40;
        that.setData({
          scollHeight: screenHeight,
          index: 1,
          activeIndex: 1,
          teams: [],
        })
        that.getTeams();
      }
    })
   
  },
  //level 二级 2 
  getTeams: function(index){
    var that = this;
    var req = {};
    if(index != null){
      req.level = index;
    }
    //
    app.ajaxJson("/agent/team",req,function(res){
      var json = res.data;
      if(json.code == 200){
        that.setData({
          teams: json.data.list || 0
        })
      }else{
        wx.showModal({
          title: '错误',
          content: '操作失败,'+json.errMsg,
        })
      }
    });
  },

  lowerview: function(){
    console.log("没有更多了")
  }
 
})