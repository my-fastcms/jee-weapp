package com.dbumama.market.utils;

import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.HashKit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;

public class EncryptUtils extends HashKit {

    /**
     * 生成新的 盐，规则：UUID的随机长度
     *
     * @return
     */
    public static String generateSalt() {
        int random = (int) (10 + (Math.random() * 10));
        return UUID.randomUUID().toString().replace("-", "").substring(random);// 随机长度
    }

    /**
     * 对密码进行 SHA256 加密
     *
     * @param password
     * @param salt
     * @return
     */
    public static String encryptPassword(String password, String salt) {
        return sha256(password + salt);
    }

    /**
     * 验证密码是否一致
     *
     * @param inputPassword 用户输入密码，网页输入的密码
     * @param salt          盐
     * @param password      数据库保存的密码
     * @return
     */
    public static boolean verlifyPassword(String inputPassword, String salt, String password) {

        if (inputPassword == null)
            return false;

        if (salt == null) {
            return false;
        }
        return password.equals(encryptPassword(inputPassword, salt));
    }
    
    //aes cbc 加密
    public static String Encrypt(String sSrc, String sKey) throws Exception {  
        if (sKey == null) {  
            System.out.print("Key为空null");  
            return null;  
        }  
        // 判断Key是否为16位  
        if (sKey.length() != 16) {  
            System.out.print("Key长度不是16位");  
            return null;  
        }  
        byte[] raw = sKey.getBytes("utf-8");  
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");  
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"  
        IvParameterSpec iv = new IvParameterSpec(sKey.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度  
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);  
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());  
        return Base64Kit.encode(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    } 
    
    //aes cbc 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {  
        try {  
            // 判断Key是否正确  
            if (sKey == null) {  
                System.out.print("Key为空null");  
                return null;  
            }  
            // 判断Key是否为16位  
            if (sKey.length() != 16) {  
                System.out.print("Key长度不是16位");  
                return null;  
            }  
            byte[] raw = sKey.getBytes("utf-8");  
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");  
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  
            IvParameterSpec iv = new IvParameterSpec(sKey.getBytes());  
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);  
            byte[] encrypted1 = Base64Kit.decode(sSrc);//先用base64解密
            try {  
                byte[] original = cipher.doFinal(encrypted1);  
                String originalString = new String(original);  
                return originalString;  
            } catch (Exception e) {  
                System.out.println(e.toString());  
                return null;  
            }  
        } catch (Exception ex) {  
            System.out.println(ex.toString());  
            return null;  
        }  
    } 

    public static void main(String[] args) {
    	String cSrc = "{data:[{'name':'你好','age':20},{'name':'zd','age':18}]}";  
        System.out.println(cSrc);  
        
    }

}
