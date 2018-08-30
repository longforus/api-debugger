package com.longforus.apidebugger.encrypt;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author liubp
 * @function 检测工具类
 */
public class DetectTool {





    /**
     * 获得一个UUID 16位
     *
     * @return String UUID
     */
    public static String getUUID() {
        String s = UUID.randomUUID().toString();
        //去掉“-”符号
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public static String getTS() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 获取手机唯一串号IMEI
     *
     * @return imei
     */
    public static String getIMEI() {
      /*  if (null == tm) {
            tm = (TelephonyManager) CommonApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        }

        return tm.getDeviceId();*/

        return getUniquePsuedoID();
    }

    //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID() {
        String serial;

        String m_szDevIDShort = "35";
            serial = "serial"; // 随便一个初始化
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();

    }



    public static String getSign(HashMap<String, String> params) {
        // 先将参数以其参数名的字典序升序进行排序
        Map<String, String> sortedParams = new TreeMap<String, String>(params);
        Set<Map.Entry<String, String>> entrys = sortedParams.entrySet();
        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder basestring = new StringBuilder();
        for (Map.Entry<String, String> param : entrys) {
            basestring.append(param.getKey()).append("=").append(chinaToUnicode(param.getValue()));
        }
        return getMD5String(basestring.toString());
    }

    /**
     * 中文转Unicode编码
     *
     * @param str 字符串
     * @return 如果是中文则返回对应的Unicode编码字符串
     */
    private static String chinaToUnicode(String str) {
        String result = "";
        String regEx = "[^\\x00-\\xff]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        boolean rs = matcher.matches();
        for (int i = 0; i < str.length(); i++) {
            int chr1 = str.charAt(i);
            if (rs) {
                result += "\\u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }

    public static String getMD5String(String basestring) {
        /*****************对排序后的参数进行MD5散列函数运算***********************/
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(basestring.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        /*****************对排序后的参数进行MD5散列函数运算***********************/
        //返回md5加密后的字符串注意统一转化为大写
        return hex.toString().toUpperCase();
    }




    /**
     * 获取凭证(Token)
     *
     * @return
     */
    public static String getToken() {
        return "666";
    }

    /**
     * 写死的版本号，对应versionName
     *
     * @return
     */
    public static String getVersionName() {
        return "1.0.0";
    }

    /**
     * 获取应用类型
     *
     * @return
     */
    public static String getAppType() {
        return "0";
    }

    /**
     * 获取设备类型，1-Android，2-IOS
     *
     * @return
     */
    public static String getType() {
        return "1";
    }

}
