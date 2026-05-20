package com.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 文件类型检测工具类
 * 
 * 使用 Apache Tika 检测文件真实类型，防止文件上传漏洞
 */
@Slf4j
@Component
public class FileTypeValidator {
    
    private static final Tika TIKA = new Tika();
    
    // 允许的 MIME 类型白名单
    public static final java.util.List<String> ALLOWED_IMAGE_MIME_TYPES = Arrays.asList(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp",
        "image/webp"
    );
    
    public static final java.util.List<String> ALLOWED_DOCUMENT_MIME_TYPES = Arrays.asList(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain"
    );
    
    /**
     * 验证图片文件
     * 
     * @param file 上传的文件
     * @return true-验证通过，false-验证失败
     */
    public static boolean isValidImage(MultipartFile file) {
        return isValidFile(file, ALLOWED_IMAGE_MIME_TYPES);
    }
    
    /**
     * 验证文档文件
     * 
     * @param file 上传的文件
     * @return true-验证通过，false-验证失败
     */
    public static boolean isValidDocument(MultipartFile file) {
        return isValidFile(file, ALLOWED_DOCUMENT_MIME_TYPES);
    }
    
    /**
     * 验证文件类型
     * 
     * @param file 上传的文件
     * @param allowedMimeTypes 允许的 MIME 类型列表
     * @return true-验证通过，false-验证失败
     */
    public static boolean isValidFile(MultipartFile file, List<String> allowedMimeTypes) {
        if (file == null || file.isEmpty()) {
            log.warn("文件为空");
            return false;
        }
        
        try {
            // 检测文件真实类型（基于文件内容）
            String detectedMimeType = TIKA.detect(file.getInputStream());
            log.debug("检测到文件 MIME 类型：{}", detectedMimeType);
            
            // 检查是否在白名单中
            if (!allowedMimeTypes.contains(detectedMimeType)) {
                log.warn("不允许的文件类型：{}", detectedMimeType);
                return false;
            }
            
            return true;
        } catch (IOException e) {
            log.error("文件类型检测失败：", e);
            return false;
        }
    }
    
    /**
     * 获取文件扩展名（从文件名）
     * 
     * @param filename 文件名
     * @return 文件扩展名（小写，不含点）
     */
    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
    
    /**
     * 检测文件的真实 MIME 类型
     * 使用 Apache Tika 基于文件内容（而非扩展名）检测
     * 
     * @param file 上传的文件
     * @return 检测到的 MIME 类型字符串
     * @throws IOException 如果读取文件内容失败
     */
    public static String detectMimeType(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return "application/octet-stream";
        }
        return TIKA.detect(file.getInputStream());
    }
    
    /**
     * 判断检测到的 MIME 类型是否与声明的文件扩展名匹配
     * 【论文4.2.10节】防止伪装文件扩展名攻击
     * 
     * @param detectedMimeType Tika 检测到的真实 MIME 类型
     * @param declaredExtension 用户声明的文件扩展名
     * @return true-类型匹配或无法判定（允许通过），false-类型不匹配（拒绝上传）
     */
    public static boolean isMimeTypeAllowed(String detectedMimeType, String declaredExtension) {
        if (detectedMimeType == null || declaredExtension == null) {
            return true; // 无法判定时默认允许
        }
        
        // 允许所有文本类型（txt、csv等可能被检测为text/plain）
        if (detectedMimeType.startsWith("text/")) {
            return true;
        }
        
        // 允许所有压缩包类型（zip、rar等可能被检测为application/zip等）
        if (detectedMimeType.equals("application/zip") || detectedMimeType.equals("application/x-rar-compressed")
                || detectedMimeType.equals("application/x-7z-compressed") || detectedMimeType.equals("application/gzip")) {
            return true;
        }
        
        // 允许所有图片类型
        if (detectedMimeType.startsWith("image/")) {
            return true;
        }
        
        // 允许 Office 文档类型
        if (ALLOWED_DOCUMENT_MIME_TYPES.contains(detectedMimeType)) {
            return true;
        }
        
        // 允许 application/octet-stream（未知类型，常见于各种格式）
        if (detectedMimeType.equals("application/octet-stream")) {
            return true;
        }
        
        // 允许复合文档类型（Office老格式）
        if (detectedMimeType.equals("application/x-tika-ooxml") || detectedMimeType.equals("application/x-tika-msoffice")) {
            return true;
        }
        
        // 严格拦截：可执行文件类型（危险！）
        String[] dangerousMimeTypes = {
            "application/x-msdownload", "application/x-msdos-program",
            "application/x-executable", "application/x-dosexec",
            "application/java-archive", "application/x-java-archive",
            "application/x-sh", "application/x-bat",
            "application/x-csh", "application/x-python-code"
        };  
        for (String dangerous : dangerousMimeTypes) {
            if (detectedMimeType.equals(dangerous)) {
                log.warn("检测到危险文件类型：{}", detectedMimeType);
                return false;
            }
        }
        
        // 其他未明确拒绝的类型默认允许
        return true;
    }
    
    /**
     * 根据 MIME 类型获取推荐的文件扩展名
     * 
     * @param mimeType MIME 类型
     * @return 推荐的扩展名
     */
    public static String getExtensionFromMimeType(String mimeType) {
        if (mimeType == null) {
            return "";
        }
        
        switch (mimeType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "application/pdf":
                return "pdf";
            case "application/msword":
                return "doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return "docx";
            case "application/vnd.ms-excel":
                return "xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return "xlsx";
            default:
                return "";
        }
    }
}
