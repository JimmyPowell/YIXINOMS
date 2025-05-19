package tech.cspioneer.backend.util;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

/**
 * 验证码工具类
 */
@Component
public class CaptchaUtils {
    
    // 使用SecureRandom更安全
    private static final Random RANDOM = new SecureRandom();
    
    // 验证码默认长度
    private static final int DEFAULT_LENGTH = 6;
    
    /**
     * 生成6位数字验证码
     * 
     * @return 6位数字验证码
     */
    public String generateCaptcha() {
        return generateCaptcha(DEFAULT_LENGTH);
    }
    
    /**
     * 生成指定长度的数字验证码
     * 
     * @param length 验证码长度
     * @return 指定长度的数字验证码
     */
    public String generateCaptcha(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }
        
        // 生成随机数字验证码
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < length; i++) {
            captcha.append(RANDOM.nextInt(10)); // 生成0-9的随机数字
        }
        
        return captcha.toString();
    }
    
    /**
     * 生成指定范围内的随机数字验证码
     * 例如：100000-999999
     * 
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return 指定范围内的随机数字验证码
     */
    public String generateCaptchaInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("最小值必须小于最大值");
        }
        
        // 计算范围
        int range = max - min + 1;
        int randomNum = RANDOM.nextInt(range) + min;
        
        return String.valueOf(randomNum);
    }
    
    /**
     * 生成固定长度的数字验证码，确保首位不为0
     * 
     * @param length 验证码长度
     * @return 首位不为0的数字验证码
     */
    public String generateNonZeroLeadingCaptcha(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }
        
        StringBuilder captcha = new StringBuilder();
        
        // 首位生成1-9
        captcha.append(RANDOM.nextInt(9) + 1);
        
        // 剩余位生成0-9
        for (int i = 1; i < length; i++) {
            captcha.append(RANDOM.nextInt(10));
        }
        
        return captcha.toString();
    }
    
    /**
     * 校验验证码是否正确（不区分大小写）
     * 
     * @param captcha 待校验的验证码
     * @param expectedCaptcha 预期的验证码
     * @return 是否匹配
     */
    public boolean validateCaptcha(String captcha, String expectedCaptcha) {
        if (captcha == null || expectedCaptcha == null) {
            return false;
        }
        
        return captcha.equals(expectedCaptcha);
    }
} 