package com.dbumama.market.utils;

import com.dbumama.market.encrypt.SHA1;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.HashKit;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class SignKit {
	
	private final static String AGLORITHM_NAME = "HmacSHA1";
	
	private final static String SEPARATOR = "&";

    public static String signShaHmac1(Map<String, String> queries, String accessSecret)
        throws InvalidKeyException, IllegalStateException {

    	 String[] sortedKeys = queries.keySet().toArray(new String[] {});
         Arrays.sort(sortedKeys);
         StringBuilder canonicalizedQueryString = new StringBuilder();
         StringBuilder stringToSign = new StringBuilder();
         try {
             for (String key : sortedKeys) {
                 canonicalizedQueryString.append("&")
                     .append(AcsURLEncoder.percentEncode(key)).append("=")
                     .append(AcsURLEncoder.percentEncode(queries.get(key)));
             }

             stringToSign.append("GET");
             stringToSign.append(SEPARATOR);
             stringToSign.append(AcsURLEncoder.percentEncode("/"));
             stringToSign.append(SEPARATOR);
             stringToSign.append(AcsURLEncoder.percentEncode(
                 canonicalizedQueryString.toString().substring(1)));

         } catch (UnsupportedEncodingException exp) {
             throw new RuntimeException("UTF-8 encoding is not supported.");
         }

        try {
            Mac mac = Mac.getInstance(AGLORITHM_NAME);
            mac.init(new SecretKeySpec(
                accessSecret.getBytes(AcsURLEncoder.URL_ENCODING), AGLORITHM_NAME));
            byte[] signData = mac.doFinal(stringToSign.toString().getBytes(AcsURLEncoder.URL_ENCODING));
            return Base64Kit.encode(signData);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("HMAC-SHA1 not supported.");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported.");
        }

    }

    public String getSignerName() {
        return "HMAC-SHA1";
    }

    public String getSignerVersion() {
        return "1.0";
    }
	
	
	/**
     * UTF-8字符集 *
     */
    public static final String CHARSET_UTF8 = "UTF-8";
    
    /**
     *  treeMap 保证按字母排序 
     * @param params
     * @return
     */
    public static String sign(TreeMap<String, Object> params) {
    	return sign(params, null);
	}
    
    public static String sign(TreeMap<String, Object> params, String secKey) {
		StringBuffer sb = new StringBuffer();
		for(String key : params.keySet()){
			String value = (String) params.get(key);
			if(!"sign".equals(key) && StringUtils.isNotBlank(value)) {
				sb.append(key + "=" + value + "&");
			}
		}
		try {
			return md5(sb.append("key=").append(secKey).toString()).toUpperCase();
		} catch (IOException e) {
			return null;
		}
	}
    
    public static String signSHA1(TreeMap<String, Object> params){
    	StringBuffer sbkey = new StringBuffer();
		for (String key : params.keySet()) {
			String value = (String) params.get(key);
			if (!"sign".equals(key) && StringUtils.isNotBlank(value)) {
				sbkey.append(key + "=" + value + "&");
			}
		}
		sbkey = sbkey.deleteCharAt(sbkey.length() - 1);
		return SHA1.getSha1(sbkey.toString());
    }
    
    /**
     *  treeMap 保证按字母排序 
     * @param params
     * @return
     */
    public static String signForShared(TreeMap<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		for(String key : params.keySet()){
			String value = (String) params.get(key);
			if(!"sign".equals(key) && StringUtils.isNotBlank(value)) {
				sb.append(key + "=" + value + "&");
			}
		}
		try {
			sb=sb.deleteCharAt(sb.length()-1);
			return md5(sb.toString()).toUpperCase();
		} catch (IOException e) {
			return null;
		}
	}
    
    public static String signForRequest(Map<String, String> params,String secret) {
		String[] keys = params.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		StringBuilder query = new StringBuilder();
		query.append(secret);
		for (String key : keys) {
			String value = params.get(key);
			if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
				query.append(key).append(value);
			}
		}
		query.append(secret);
		return HashKit.md5(query.toString()).toUpperCase();
	}

    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    
    public static String genRandomString32(){
    	return getRandomStringByLength(32);
    }
    
    public static String md5(String source) throws IOException{
    	return byte2hex(encryptMD5(source));
    }
	
	private static String getStringFromException(Throwable e) {
        String result = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos);
        e.printStackTrace(ps);
        try {
            result = bos.toString(CHARSET_UTF8);
        } catch (IOException ioe) {
        }
        return result;
    }
	
	private static byte[] encryptMD5(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(data.getBytes(CHARSET_UTF8));
        } catch (GeneralSecurityException gse) {
            String msg = getStringFromException(gse);
            throw new IOException(msg);
        }
        return bytes;
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    static class AcsURLEncoder {
        public final static String URL_ENCODING = "UTF-8";

        public static String encode(String value) throws UnsupportedEncodingException {
            return URLEncoder.encode(value, URL_ENCODING);
        }

        public static String percentEncode(String value) throws UnsupportedEncodingException {
            return value != null ? URLEncoder.encode(value, URL_ENCODING).replace("+", "%20")
                    .replace("*", "%2A").replace("%7E", "~") : null;
        }
    }
    
}
