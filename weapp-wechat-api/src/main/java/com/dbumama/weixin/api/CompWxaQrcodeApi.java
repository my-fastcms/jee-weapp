package com.dbumama.weixin.api;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.dbumama.market.utils.JsonUtils;
import com.dbumama.weixin.utils.HttpUtils;
import com.jfinal.kit.Kv;

/**
 * @author wangjun
 * 2018年11月7日
 */
public class CompWxaQrcodeApi {
    private static String getWxaCodeUrl = "https://api.weixin.qq.com/wxa/getwxacode?access_token=";
    
    /**
     * 获取小程序码
     *
     * 接口A: 适用于需要的码数量较少的业务场景
     *
     * width 默认430
     * @param path 不能为空，最大长度 128 字节
     * @return InputStream
     */
    public InputStream get(String accessToken, String path) {
        return get(accessToken, path, 430);
    }

    /**
     * 获取小程序码
     *
     * 接口A: 适用于需要的码数量较少的业务场景
     *
     * @param path 不能为空，最大长度 128 字节
     * @param width 默认430
     * @return InputStream
     */
    public InputStream get(String accessToken, String path, int width) {
        return get(accessToken, path, 430, true, null);
    }

    /**
     * 获取小程序码
     *
     * 接口A: 适用于需要的码数量较少的业务场景
     *
     * width 默认430
     * @param path 不能为空，最大长度 128 字节
     * @param width 默认430
     * @param r 颜色R
     * @param g 颜色R
     * @param b 颜色B
     * @return InputStream
     */
    public InputStream get(String accessToken, String path, int width, String r, String g, String b) {
        Map<String, String> lineColor = new HashMap<String, String>();
        lineColor.put("r", r);
        lineColor.put("g", g);
        lineColor.put("b", b);
        return get(accessToken, path, 430, false, lineColor);
    }

    private InputStream get(String accessToken, String path, int width, boolean autoColor, Map<String, String> lineColor) {
        Kv kv = Kv.by("page", path)
                .set("width", String.valueOf(width))
                .set("auto_color", autoColor)
                .set("line_color", lineColor);
        String url = getWxaCodeUrl + accessToken;
        return HttpUtils.download(url, JsonUtils.toJson(kv));
    }
    
    private static String getWxaCodeUnLimitURL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=";
    
    
    /**
     * 获取小程序码
     *
     * 接口B: 适用于需要的码数量极多，或仅临时使用的业务场景
     *
     * @param scene 场景
     * @param path 不能为空，最大长度 128 字节
     * @return InputStream
     */
    public static InputStream getUnLimit(String accessToken, String scene, String path) {
        return getUnLimit(accessToken, scene, path, 430);
    }

    /**
     * 获取小程序码
     *
     * 接口B: 适用于需要的码数量极多，或仅临时使用的业务场景
     *
     * @param scene 场景
     * @param path 不能为空，最大长度 128 字节
     * @param width 默认430
     * @return InputStream
     */
    public static InputStream getUnLimit(String accessToken, String scene, String path, int width) {
        return getUnLimit(accessToken, scene, path, 430, true, null);
    }

    /**
     * 获取小程序码
     *
     * 接口B: 适用于需要的码数量极多，或仅临时使用的业务场景
     *
     * @param scene 场景
     * @param path 不能为空，最大长度 128 字节
     * @param width 默认430
     * @param r 颜色R
     * @param g 颜色R
     * @param b 颜色B
     * @return InputStream
     */
    public InputStream getUnLimit(String accessToken, String scene, String path, int width, String r, String g, String b) {
        Map<String, String> lineColor = new HashMap<String, String>();
        lineColor.put("r", r);
        lineColor.put("g", g);
        lineColor.put("b", b);
        return getUnLimit(accessToken, scene, path, 430, false, lineColor);
    }

    private static InputStream getUnLimit(String accessToken, String scene, String path, int width, boolean autoColor, Map<String, String> lineColor) {
        Kv kv = Kv.by("page", path)
                .set("scene", scene)
                .set("width", String.valueOf(width))
                .set("is_hyaline", true)//透明底
                .set("auto_color", autoColor)
                .set("line_color", lineColor);
        String url = getWxaCodeUnLimitURL + accessToken;
        return HttpUtils.download(url, JsonUtils.toJson(kv));
    }

    // 文档地址:https://mp.weixin.qq.com/debug/wxadoc/dev/api/qrcode.html
    private static String createWxaQrcodeURL = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token=";

    /**
     * 获取小程序页面二维码
     *
     * 通过该接口，仅能生成已发布的小程序的二维码。
     * 可以在开发者工具预览时生成开发版的带参二维码。
     * 带参二维码只有 100000 个，请谨慎调用。
     *
     * width 默认430
     * @param path 不能为空，最大长度 128 字节
     * @return InputStream
     */
    public InputStream createQrcode(String accessToken, String path) {
        return createQrcode(accessToken, path, 430);
    }

    /**
     * 获取小程序页面二维码
     *
     * 通过该接口，仅能生成已发布的小程序的二维码。
     * 可以在开发者工具预览时生成开发版的带参二维码。
     * 带参二维码只有 100000 个，请谨慎调用。
     *
     * @param path 不能为空，最大长度 128 字节
     * @param width 默认430 二维码的宽度
     * @return InputStream
     */
    public InputStream createQrcode(String accessToken, String path, int width) {
        String url = createWxaQrcodeURL + accessToken;
        ParaMap pm = ParaMap.create("path", path).put("width", String.valueOf(width));
        return HttpUtils.download(url, JsonUtils.toJson(pm.getData()));
    }
}
