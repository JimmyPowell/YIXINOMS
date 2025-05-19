package tech.cspioneer.backend.util;

import java.util.UUID;

/**
 * UUID工具类
 */
public class UuidUtils {
    
    /**
     * 生成标准UUID（带分隔符）
     * 格式：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     * 
     * @return 带分隔符的UUID字符串
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 生成不带分隔符的UUID
     * 格式：xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
     * 
     * @return 不带分隔符的UUID字符串
     */
    public static String generateUuidWithoutDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 根据输入的字符串生成UUID（带分隔符）
     * 
     * @param name 输入字符串
     * @return 带分隔符的UUID字符串
     */
    public static String generateUuidFromString(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes()).toString();
    }
    
    /**
     * 根据输入的字符串生成UUID（不带分隔符）
     * 
     * @param name 输入字符串
     * @return 不带分隔符的UUID字符串
     */
    public static String generateUuidFromStringWithoutDash(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes()).toString().replace("-", "");
    }
    
    /**
     * 检查字符串是否为有效的UUID格式（带分隔符）
     * 
     * @param uuidStr 待检查的UUID字符串
     * @return 是否为有效的UUID
     */
    public static boolean isValidUuid(String uuidStr) {
        if (uuidStr == null) {
            return false;
        }
        
        try {
            UUID.fromString(uuidStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
} 