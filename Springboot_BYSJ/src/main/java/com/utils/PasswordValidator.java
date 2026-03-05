package com.utils;

import org.springframework.util.StringUtils;
import java.util.regex.Pattern;

/**
 * 密码强度校验工具类（增强版）
 * 
 * 【优化说明】
 * 1. 增加密码复杂度要求（大小写 + 数字 + 特殊字符）
 * 2. 提供密码强度评分
 * 3. 给出友好的错误提示
 * 4. 支持毕业设计和生产环境两种模式
 * 
 * 【使用说明】
 * - 毕业设计模式：使用 isValidSimple() 方法，仅检查长度
 * - 生产环境模式：使用 isValid() 方法，严格校验
 */
public class PasswordValidator {
    
    // ==================== 常量定义 ====================
    
    /** 最小密码长度（生产环境） */
    private static final int MIN_LENGTH = 8;
    
    /** 最大密码长度 */
    private static final int MAX_LENGTH = 20;
    
    /** 毕业设计模式最小长度（便于测试） */
    private static final int MIN_LENGTH_SIMPLE = 6;
    
    // 正则表达式模式
    private static final Pattern UPPER_CASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWER_CASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR = Pattern.compile("[^A-Za-z0-9]");
    
    // 常见弱密码列表
    private static final String[] WEAK_PASSWORDS = {
        "123456", "password", "admin", "qwerty", 
        "12345678", "123456789", "iloveyou", "111111", "000000"
    };
    
    // ==================== 公开方法 ====================
    
    /**
     * 验证密码是否符合要求（严格模式 - 生产环境）
     * 
     * @param password 待验证的密码
     * @return true-符合要求，false-不符合要求
     */
    public static boolean isValid(String password) {
        return validateAndGetError(password) == null;
    }
    
    /**
     * 验证密码是否符合要求（简单模式 - 毕业设计演示）
     * 仅检查长度，便于测试和演示
     * 
     * @param password 待验证的密码
     * @return true-符合要求，false-不符合要求
     */
    public static boolean isValidSimple(String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }
        return password.length() >= MIN_LENGTH_SIMPLE;
    }
    
    /**
     * 验证密码并返回错误信息（严格模式）
     * 
     * @param password 待验证的密码
     * @return 错误信息，如果验证通过则返回 null
     */
    public static String validateAndGetError(String password) {
        // 空值检查
        if (!StringUtils.hasText(password)) {
            return "密码不能为空";
        }
        
        // 长度检查
        if (password.length() < MIN_LENGTH) {
            return "密码长度不能少于 " + MIN_LENGTH + " 位";
        }
        
        if (password.length() > MAX_LENGTH) {
            return "密码长度不能超过 " + MAX_LENGTH + " 位";
        }
        
        // 复杂度检查（至少包含三种字符）
        int matchCount = 0;
        boolean hasUpper = UPPER_CASE.matcher(password).find();
        boolean hasLower = LOWER_CASE.matcher(password).find();
        boolean hasDigit = DIGIT.matcher(password).find();
        boolean hasSpecial = SPECIAL_CHAR.matcher(password).find();
        
        if (hasUpper) matchCount++;
        if (hasLower) matchCount++;
        if (hasDigit) matchCount++;
        if (hasSpecial) matchCount++;
        
        if (matchCount < 3) {
            return "密码必须包含大写字母、小写字母、数字、特殊字符中的至少三种";
        }
        
        // 常见弱密码检查
        if (isWeakPassword(password)) {
            return "密码过于简单，请勿使用常见弱密码";
        }
        
        return null;
    }
    
    /**
     * 获取密码强度评分（0-100）
     * 
     * @param password 密码
     * @return 强度评分
     */
    public static int getStrengthScore(String password) {
        if (!StringUtils.hasText(password)) {
            return 0;
        }
        
        int score = 0;
        
        // 基础分（长度）
        score += Math.min(password.length() * 4, 40);
        
        // 复杂度分
        if (UPPER_CASE.matcher(password).find()) score += 10;
        if (LOWER_CASE.matcher(password).find()) score += 10;
        if (DIGIT.matcher(password).find()) score += 10;
        if (SPECIAL_CHAR.matcher(password).find()) score += 20;
        
        // 额外加分（长度超过 12）
        if (password.length() >= 12) {
            score += 10;
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * 获取密码强度等级描述
     * 
     * @param password 密码
     * @return 强度等级（强/中/弱/极弱）
     */
    public static String getStrengthLevel(String password) {
        int score = getStrengthScore(password);
        
        if (score >= 80) {
            return "强";
        } else if (score >= 60) {
            return "中";
        } else if (score >= 40) {
            return "弱";
        } else {
            return "极弱";
        }
    }
    
    /**
     * 获取密码要求说明（严格模式）
     * 
     * @return 密码要求说明字符串
     */
    public static String getRequirements() {
        return "密码要求:\n" +
               "1. 长度不少于 " + MIN_LENGTH + " 位，不超过 " + MAX_LENGTH + " 位\n" +
               "2. 必须包含大写字母、小写字母、数字、特殊字符中的至少三种\n" +
               "3. 不能使用常见弱密码（如 123456、password 等）\n" +
               "4. 不能包含连续字符（如 123、abc 等）";
    }
    
    /**
     * 获取密码要求说明（简单模式）
     * 
     * @return 密码要求说明字符串
     */
    public static String getRequirementsSimple() {
        return "密码长度不能少于 " + MIN_LENGTH_SIMPLE + " 位";
    }
    
    // ==================== 私有方法 ====================
    
    /**
     * 检查是否为弱密码
     * 
     * @param password 密码
     * @return true-是弱密码，false-不是弱密码
     */
    private static boolean isWeakPassword(String password) {
        String lowerPwd = password.toLowerCase();
        
        // 检查常见弱密码
        for (String weak : WEAK_PASSWORDS) {
            if (lowerPwd.equals(weak) || lowerPwd.contains(weak)) {
                return true;
            }
        }
        
        // 检查连续数字
        if (password.matches(".*(012|123|234|345|456|567|678|789).*")) {
            return true;
        }
        
        // 检查连续字母
        if (password.matches(".*(abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz).*")) {
            return true;
        }
        
        // 检查重复字符（如 aaaaaa）
        if (password.matches("(.)\\1{2,}")) {
            return true;
        }
        
        return false;
    }
}
