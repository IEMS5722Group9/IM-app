package hk.edu.cuhk.ie.iems5722.a2_1155149902.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class MD5Util {
    /**
     * 给指定字符串按照md5算法去加密
     *
     * @param psd 需要加密的密码    加盐处理
     * @return md5后的字符串
     */
    public static String encoder(String psd, String salt) {
        try {
            //加盐处理
            psd = psd + salt;
            //1 指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //2 将需要加密的字符串中转换成byte类型的数组,然后进行随机哈希过程
            byte[] bs = digest.digest(psd.getBytes());
            //3 循环遍历bs,然后让其生成32位字符串,固定写法
            //4 拼接字符串过程
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bs) {
                int i = b & 0xff;
                //int类型的i需要转换成16机制字符
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                stringBuffer.append(hexString);
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 生成含有随机盐的密码
     */
    public static String generateSalt(String password) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append('0');
            }
        }
        String salt = sb.toString();
        return salt;
    }

}
