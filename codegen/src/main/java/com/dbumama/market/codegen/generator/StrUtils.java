/**
 * Copyright (c) 广州点步信息科技有限公司 2016-2017, wjun_java@163.com.
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *	    http://www.dbumama.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dbumama.market.codegen.generator;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.core.JFinal;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

/**
 * @author wangjun
 *
 * 2019年5月31日
 */
public final class StrUtils extends StrKit{

	private static final Log log = Log.getLog(StrUtils.class);

	public static String toLowerCase(String str){
		return str.toLowerCase();
	}
	
	public static String formartType(String str){
		return str.substring(str.lastIndexOf(".")+1, str.length());
	}
	
    public static String urlDecode(String string) {
        try {
            return URLDecoder.decode(string, JFinal.me().getConstants().getEncoding());
        } catch (UnsupportedEncodingException e) {
            log.error("urlDecode is error", e);
        }
        return string;
    }

    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, JFinal.me().getConstants().getEncoding());
        } catch (UnsupportedEncodingException e) {
            log.error("urlEncode is error", e);
        }
        return string;
    }

    public static String urlRedirect(String redirect) {
        try {
            redirect = new String(redirect.getBytes(JFinal.me().getConstants().getEncoding()), "ISO8859_1");
        } catch (UnsupportedEncodingException e) {
            log.error("urlRedirect is error", e);
        }
        return redirect;
    }

    public static boolean areNotEmpty(String... strings) {
        if (strings == null || strings.length == 0)
            return false;

        for (String string : strings) {
            if (string == null || "".equals(string)) {
                return false;
            }
        }
        return true;
    }

    public static String requireNonBlank(String string) {
        if (isBlank(string))
            throw new NullPointerException();
        return string;
    }

    public static String requireNonBlank(String string, String message) {
        if (isBlank(string))
            throw new NullPointerException(message);
        return string;
    }

    public static String obtainDefaultIfBlank(String string, String defaultValue) {
        return isBlank(string) ? defaultValue : string;
    }

    /**
     * 不是空数据，注意：空格不是空数据
     *
     * @param string
     * @return
     */
    public static boolean isNotEmpty(String string) {
        return string != null && !string.equals("");
    }


    /**
     * 确保不是空白字符串
     *
     * @param o
     * @return
     */
    public static boolean isNotBlank(Object o) {
        return o == null ? false : notBlank(o.toString());
    }


    /**
     * 字符串是否匹配某个正则
     *
     * @param string
     * @param regex
     * @return
     */
    public static boolean match(String string, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }


    /**
     * 这个字符串是否是全是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null)
            return false;
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    /**
     * 这个字符串是否是小数点
     *
     * @param str
     * @return
     */
    public static boolean isDecimal(String str) {
        if (str == null)
            return false;
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if ((chr < 48 || chr > 57) && chr != '.')
                return false;
        }
        return true;
    }

    /**
     * 是否是邮件的字符串
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        return Pattern.matches("\\w+@(\\w+.)+[a-z]{2,3}", email);
    }


    /**
     * 是否是中国地区手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isMobileNumber(String phoneNumber) {
        return Pattern.matches("^(1[3,4,5,6,7,8,9])\\d{9}$", phoneNumber);
    }


    /**
     * 生成一个新的UUID
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 把字符串拆分成一个set
     *
     * @param src
     * @param regex
     * @return
     */
    public static Set<String> splitToSet(String src, String regex) {
        if (src == null) {
            return null;
        }

        String[] strings = src.split(regex);
        Set<String> set = new HashSet<>();
        for (String s : strings) {
            if (StrUtils.isBlank(s)) {
                continue;
            }
            set.add(s.trim());
        }
        return set;
    }


    private static final String[] htmlChars = {"&", "<", ">", "'", "\""};
    private static final String[] escapeChars = {"&amp;", "&lt;", "&gt;", "&#39;", "&quot;"};

    public static String escapeHtml(String content) {
        return isBlank(content) ? content : StringUtils.replaceEach(unEscapeHtml(content), htmlChars, escapeChars);
    }

    public static String unEscapeHtml(String content) {
        return isBlank(content) ? content : StringUtils.replaceEach(content, escapeChars, htmlChars);
    }


    public static void main(String[] args) {
        String url = "http://www.baidu.com?username=aaa";
        System.out.println(StrUtils.urlEncode(url));
    }
	
}
