package com.dbumama.market.web.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;

import io.jboot.utils.StrUtil;

/**
 * 工具类 - 公用
 *
 */

public class CommonUtil {

	/**
	 * 随机获取UUID字符串(无中划线)
	 * 
	 * @return UUID字符串
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24);
	}
	
	/**
	 * 随机获取字符串
	 * 
	 * @param length
	 *            随机字符串长度
	 * 
	 * @return 随机字符串
	 */
	public static String getRandomString(int length) {
		if (length <= 0) {
			return "";
		}
		char[] randomChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd',
				'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm' };
		Random random = new Random();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			stringBuffer.append(randomChar[Math.abs(random.nextInt()) % randomChar.length]);
		}
		return stringBuffer.toString();
	}

	/**
	 * 根据指定长度 分隔字符串
	 * 
	 * @param str
	 *            需要处理的字符串
	 * @param length
	 *            分隔长度
	 * 
	 * @return 字符串集合
	 */
	public static List<String> splitString(String str, int length) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < str.length(); i += length) {
			int endIndex = i + length;
			if (endIndex <= str.length()) {
				list.add(str.substring(i, i + length));
			} else {
				list.add(str.substring(i, str.length() - 1));
			}
		}
		return list;
	}

	/**
	 * 将字符串List转化为字符串，以分隔符间隔.
	 * 
	 * @param list
	 *            需要处理的List.
	 *            
	 * @param separator
	 *            分隔符.
	 * 
	 * @return 转化后的字符串
	 */
	public static String toString(List<String> list, String separator) {
		StringBuffer stringBuffer = new StringBuffer();
		for (String str : list) {
			stringBuffer.append(separator + str);
		}
		stringBuffer.deleteCharAt(0);
		return stringBuffer.toString();
	}

	/**
     * 检测是否有emoji字符
     * @param source
     * @return 一旦含有就抛出
     */
    public static boolean containsEmoji(String source) {
        if (StringUtils.isBlank(source)) {
            return false;
        }
 
        int len = source.length();
 
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
 
            if (isEmojiCharacter(codePoint)) {
                //do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }
 
        return false;
    }
 
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }
 
    /**
     * 过滤emoji 或者 其他非文字类型的字符
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
    	return source == null ? "" : source.replaceAll("[\\x{10000}-\\x{10FFFF}]", "");
    }
    
    public static String subString(String source, int len){
    	if(StrKit.isBlank(source)) return "";
    	
    	if(source.length() <= len) return source;
    		
    	return source.substring(0, len).concat("...");
    	
    }
    
    public static String generateCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt(9999 - 1000 + 1) + 1000);
    }

    public static void quietlyClose(AutoCloseable... autoCloseables) {
        for (AutoCloseable closeable : autoCloseables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }


    public static String maxLength(String content, int maxLength) {
        if (StrUtil.isBlank(content)) {
            return content;
        }

        if (maxLength <= 0) {
            throw new IllegalArgumentException("maxLength 必须大于 0 ");
        }

        return content.length() <= maxLength ? content :
                content.substring(0, maxLength);

    }


    public static String maxLength(String content, int maxLength, String suffix) {
        if (StrUtil.isBlank(suffix)) {
            return maxLength(content, maxLength);
        }

        if (StrUtil.isBlank(content)) {
            return content;
        }

        if (maxLength <= 0) {
            throw new IllegalArgumentException("maxLength 必须大于 0 ");
        }

        return content.length() <= maxLength ? content :
                content.substring(0, maxLength) + suffix;

    }

    public static String removeSuffix(String url) {

        int indexOf = url.indexOf(".");

        if (indexOf == -1) {
            return url;
        }

        return url.substring(0, indexOf);
    }

    /**
     * 防止 model 存储关于 xss 相关代码
     *
     * @param model
     */
    public static void escapeHtmlForAllAttrs(@SuppressWarnings("rawtypes") Model model, String... ignoreAttrs) {
        String[] attrNames = model._getAttrNames();
        for (String attr : attrNames) {

            if (ArrayUtils.contains(ignoreAttrs, attr)) {
                continue;
            }

            Object value = model.get(attr);

            if (value != null && value instanceof String) {
                model.set(attr, StrUtil.escapeHtml(value.toString()));
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(generateCode());
    }
    
}