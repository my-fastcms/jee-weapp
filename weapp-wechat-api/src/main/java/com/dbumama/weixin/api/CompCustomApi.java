/**
 * Copyright (c) 2011-2015, Unas 小强哥 (unas@qq.com).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.dbumama.weixin.api;

import com.dbumama.market.base.ApiResult;
import com.dbumama.market.utils.JsonUtils;
import com.dbumama.weixin.utils.HttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多客服功能</br>
 * 仅支持获取客服聊天记录接口，其他功能可以使用微信官方的多客服客户端软件来完成。
 * 
 * 客服接口：http://mp.weixin.qq.com/wiki/1/70a29afed17f56d537c833f89be979c9.html
 */
public class CompCustomApi {

    private static String getRecordUrl = "https://api.weixin.qq.com/customservice/msgrecord/getrecord?access_token=";

    /**
     * 获取客服聊天记录
     */
    public static ApiResult getRecord(String accessToken, String jsonStr) {
        String jsonResult = HttpUtils.post(getRecordUrl + accessToken, jsonStr);
        return new ApiResult(jsonResult);
    }

    private static String addKfAccountUrl = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=";
    
    /**
     * 添加客服帐号
     * @param kf_account 完整客服账号，格式为：账号前缀@公众号微信号
     * @param nickname 客服昵称，最长6个汉字或12个英文字符
     * @param password 客服账号登录密码，格式为密码明文的32位加密MD5值。该密码仅用于在公众平台官网的多客服功能中使用，若不使用多客服功能，则不必设置密码
     * @return ApiResult
     */
    public static ApiResult addKfAccount(String accessToken, String kf_account, String nickname, String password) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("kf_account", kf_account);
        params.put("nickname", nickname);
        params.put("password", password);
        
        String jsonResult = HttpUtils.post(addKfAccountUrl + accessToken, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }
    
    private static String updateKfAccountUrl = "https://api.weixin.qq.com/customservice/kfaccount/update?access_token=";
    
    /**
     * 修改客服帐号
     * @param kf_account 完整客服账号，格式为：账号前缀@公众号微信号
     * @param nickname 客服昵称，最长6个汉字或12个英文字符
     * @param password 客服账号登录密码，格式为密码明文的32位加密MD5值。该密码仅用于在公众平台官网的多客服功能中使用，若不使用多客服功能，则不必设置密码
     * @return ApiResult
     */
    public static ApiResult updateKfAccount(String accessToken, String kf_account, String nickname, String password) {
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("kf_account", kf_account);
        params.put("nickname", nickname);
        params.put("password", password);
        
        String jsonResult = HttpUtils.post(updateKfAccountUrl + accessToken, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }
    
    private static String delKfAccountUrl = "https://api.weixin.qq.com/customservice/kfaccount/del?access_token=";
    
    /**
     * 删除客服帐号
     * @param kf_account 完整客服账号，格式为：账号前缀@公众号微信号
     * @param nickname 客服昵称，最长6个汉字或12个英文字符
     * @param password 客服账号登录密码，格式为密码明文的32位加密MD5值。该密码仅用于在公众平台官网的多客服功能中使用，若不使用多客服功能，则不必设置密码
     * @return ApiResult
     */
    public static ApiResult delKfAccount(String accessToken, String kf_account, String nickname, String password) {
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("kf_account", kf_account);
        params.put("nickname", nickname);
        params.put("password", password);
        
        String jsonResult = HttpUtils.post(delKfAccountUrl + accessToken, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }
    
    private static String uploadKfAccountHeadImgUrl = "http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=";
    
    /**
     * 设置客服帐号的头像
     * @param kf_account 完整客服账号，格式为：账号前缀@公众号微信号
     * @param headImg 客服人员的头像，头像图片文件必须是jpg格式，推荐使用640*640大小的图片以达到最佳效果
     * @return
     */
    public static ApiResult uploadKfAccountHeadImg(String accessToken, String kf_account, File headImg) {
        String url = uploadKfAccountHeadImgUrl + accessToken + "&kf_account=" + kf_account;
        String jsonResult = HttpUtils.upload(url, headImg, null);
        return new ApiResult(jsonResult);
    }
    
    private static String getKfListUrl = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=";
    
    /**
     * 获取所有客服账号
     * @return ApiResult
     */
    public static ApiResult getKfList(String accessToken) {
        String jsonResult = HttpUtils.get(getKfListUrl + accessToken);
        return new ApiResult(jsonResult);
    }
    
    private static String getOnlineKfListUrl = "https://api.weixin.qq.com/cgi-bin/customservice/getonlinekflist?access_token=";
    
    public static ApiResult getKfOnlineList(String accessToken) {
    	String jsonResult = HttpUtils.get(getOnlineKfListUrl + accessToken);
        return new ApiResult(jsonResult);
    }
    
    private static String customMessageUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";

    /**
     * 发送客服消息
     * @param message
     * @return ApiResult
     */
    private static ApiResult sendMsg(String accessToken, Map<String, Object> message, String authAppId) {
        String jsonResult = HttpUtils.post(customMessageUrl + accessToken + "&appid="+authAppId, JsonUtils.toJson(message));
        return new ApiResult(jsonResult);
    }

    /**
     * <xml>
 <ToUserName><![CDATA[toUser]]></ToUserName>
 <FromUserName><![CDATA[fromUser]]></FromUserName>
 <CreateTime>1348831860</CreateTime>
 <MsgType><![CDATA[text]]></MsgType>
 <Content><![CDATA[this is a test]]></Content>
 <MsgId>1234567890123456</MsgId>
 </xml>
     * 发送文本客服消息
     * @param openId
     * @param text
     */
    public static ApiResult sendText(String accessToken, String openId, String text, String authAppId) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "text");

        Map<String, Object> textObj = new HashMap<String, Object>();
        textObj.put("content", text);

        json.put("text", textObj);
        return sendMsg(accessToken, json, authAppId);
    }
    
    /**
     * 发送图片消息
     * @param openId
     * @param media_id
     * @return
     */
    public static ApiResult sendImage(String accessToken, String openId, String media_id, String authAppId) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "image");

        Map<String, Object> image = new HashMap<String, Object>();
        image.put("media_id", media_id);

        json.put("image", image);
        return sendMsg(accessToken, json, authAppId);
    }

    /**
     * 发送语言回复
     * @param openId
     * @param media_id
     * @return
     */
    public static ApiResult sendVoice(String accessToken, String openId, String media_id, String authAppId) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "voice");

        Map<String, Object> voice = new HashMap<String, Object>();
        voice.put("media_id", media_id);

        json.put("voice", voice);
        return sendMsg(accessToken, json, authAppId);
    }

    /**
     * 发送视频回复
     * @param openId
     * @param media_id
     * @param title
     * @param description
     * @return
     */
    public static ApiResult sendVideo(String accessToken, String openId, String media_id, String title, String description, String authAppId) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "video");

        Map<String, Object> video = new HashMap<String, Object>();
        video.put("media_id", media_id);
        video.put("title", title);
        video.put("description", description);

        json.put("video", video);
        return sendMsg(accessToken, json, authAppId);
    }

    /**
     * 发送音乐回复
     * @param openId
     * @param musicurl
     * @param hqmusicurl
     * @param thumb_media_id
     * @param title
     * @param description
     * @return
     */
    public static ApiResult sendMusic(String accessToken, String openId, String musicurl, String hqmusicurl, String thumb_media_id, String title, String description, String authAppId) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "music");

        Map<String, Object> music = new HashMap<String, Object>();
        music.put("musicurl", musicurl);
        music.put("hqmusicurl", hqmusicurl);
        music.put("thumb_media_id", thumb_media_id);
        music.put("title", title);
        music.put("description", description);

        json.put("music", music);
        return sendMsg(accessToken, json, authAppId);
    }

    /**
     * 发送图文回复
     * @param openId
     * @param articles
     * @return
     */
    public static ApiResult sendNews(String accessToken, String openId, List<Articles> articles, String authAppId) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "news");

        Map<String, Object> news = new HashMap<String, Object>();
        news.put("articles", articles);

        json.put("news", news);
        return sendMsg(accessToken, json, authAppId);
    }
    
    /**
 <xml>
<ToUserName><![CDATA[toUser]]></ToUserName>
<FromUserName><![CDATA[fromUser]]></FromUserName>
<CreateTime>1351776360</CreateTime>
<MsgType><![CDATA[link]]></MsgType>
<Title><![CDATA[公众平台官网链接]]></Title>
<Description><![CDATA[公众平台官网链接]]></Description>
<Url><![CDATA[url]]></Url>
<MsgId>1234567890123456</MsgId>
</xml>
     * 发送链接消息
     * @param openId
     * @param text
     * @return
     */
    public static ApiResult sendLink(String accessToken, String openId, String title, String desc, String url, String authAppId) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "link");

        Map<String, Object> link = new HashMap<String, Object>();
        link.put("title", title);
        link.put("description", desc);
        link.put("url", url);
        json.put("link", link);
        return sendMsg(accessToken, json, authAppId);
    }
    
    public static ApiResult sendMiniprog(String accessToken, String openId, String title, String appid, String pagepath, String thumb_media_id, String authAppId){
    	Map<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "miniprogrampage");

        Map<String, Object> miniprogrampage = new HashMap<String, Object>();
        miniprogrampage.put("thumb_media_id", thumb_media_id);
        miniprogrampage.put("title", title);
        miniprogrampage.put("appid", appid);
        miniprogrampage.put("pagepath", pagepath);

        json.put("miniprogrampage", miniprogrampage);
        
        return sendMsg(accessToken, json, authAppId);
    }

    /**
     * 客户消息图文封装和 `News` 又略微区别，无法公用
     */
    public static class Articles {
        private String title;
        private String description;
        private String url;
        private String picurl;

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public String getPicurl() {
            return picurl;
        }
        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }

    /**
     * 发送卡券
     * @param openId
     * @param card_id
     * @param card_ext 详情及签名规则: http://mp.weixin.qq.com/wiki/7/aaa137b55fb2e0456bf8dd9148dd613f.html#.E9.99.84.E5.BD.954-.E5.8D.A1.E5.88.B8.E6.89.A9.E5.B1.95.E5.AD.97.E6.AE.B5.E5.8F.8A.E7.AD.BE.E5.90.8D.E7.94.9F.E6.88.90.E7.AE.97.E6.B3.95
     * @return
     */
    public static ApiResult sendCoupon(String accessToken, String openId, String card_id, String card_ext, String authAppId) {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("touser", openId);
        json.put("msgtype", "wxcard");

        Map<String, Object> wxcard = new HashMap<String, Object>();
        wxcard.put("card_id", card_id);
        wxcard.put("card_ext", card_ext);

        json.put("wxcard", wxcard);
        return sendMsg(accessToken, json, authAppId);
    }
    
    public static void main(String[] args) {
	}

}
