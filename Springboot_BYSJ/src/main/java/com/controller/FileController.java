package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.entity.EIException;
import com.service.ConfigService;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文件上传/下载 Controller
 * 
 * 【毕业设计优化版】
 * 功能：处理文件上传和下载请求
 * 特点：
 * - 支持常见图片、文档格式
 * - 文件大小限制（默认 10MB）
 * - 兼容 IDE 和 jar包运行
 */
@RestController
@RequestMapping("file")
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class FileController {
    
    @Autowired
    private ConfigService configService;

    @Autowired
    private ServletContext servletContext;  // 用于兼容 jar包运行时的静态资源路径

    // 从配置文件读取上传相关参数
    @Value("${file.upload.max-size:10485760}")  // 默认 10MB（字节）
    private long maxFileSize;

    @Value("${file.upload.allow-types:jpg,png,jpeg,gif,pdf,doc,docx}")
    private String allowFileTypes;

    @Value("${file.upload.path:./upload/}")  // 上传根路径，可配置
    private String uploadRootPath;

    /**
     * 上传文件
     * 
     * 【毕业设计简化版】
     * 流程：
     * 1. 检查文件是否为空
     * 2. 检查文件大小
     * 3. 检查文件类型（通过后缀名）
     * 4. 保存文件到服务器
     * 
     * @param file 上传的文件
     * @param type 文件类型标识（可选）
     * @return 返回文件名
     */
    @OperationLog("上传文件")
    @PostMapping("/upload")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String type) throws Exception {

        // 1. 空文件校验
        if (file.isEmpty()) {
            log.warn("文件上传失败：上传文件为空");
            throw new EIException("上传文件不能为空");
        }

        // 2. 文件大小校验
        if (file.getSize() > maxFileSize) {
            log.warn("文件上传失败：文件大小超过限制，当前大小：{}，限制：{}",
                    file.getSize(), maxFileSize);
            throw new EIException("文件大小超过限制（最大" + (maxFileSize/1024/1024) + "MB）");
        }

        // 3. 文件类型校验（基于后缀名）
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            log.warn("文件上传失败：文件名非法，无后缀");
            throw new EIException("文件名称非法，请上传带后缀的文件");
        }
        String fileExt = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowTypes = Arrays.asList(allowFileTypes.split(","));
        if (!allowTypes.contains(fileExt)) {
            log.warn("文件上传失败：不支持的文件类型，当前类型：{}，支持类型：{}",
                    fileExt, allowFileTypes);
            throw new EIException("不支持该文件类型，仅支持：" + allowFileTypes);
        }

        // 4. 构建上传目录（兼容 IDE 和 jar包运行）
        File uploadDir = getUploadDir();
        if (!uploadDir.exists()) {
            boolean mkdirs = uploadDir.mkdirs();
            if (!mkdirs) {
                log.error("文件上传失败：创建上传目录失败，路径：{}", uploadDir.getAbsolutePath());
                throw new EIException("创建上传目录失败，请检查服务器权限");
            }
        }

        // 5. 生成唯一文件名（时间戳 + 原始文件名，避免冲突并保留原名）
        String timestamp = String.valueOf(new Date().getTime());
        // 移除原始文件名中的特殊字符，保留中文和字母数字
        String safeOriginalName = originalFilename.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9._-]", "_");
        // 生成格式：时间戳_原始文件名.扩展名
        String fileName = timestamp + "_" + safeOriginalName;
        File destFile = new File(uploadDir, fileName);

        try {
            // 6. 保存文件
            Files.copy(file.getInputStream(), destFile.toPath());
            log.info("文件上传成功：{} -> {}", originalFilename, destFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("文件上传失败：保存文件出错，文件名：{}，异常：{}",
                    originalFilename, e.getMessage(), e);
            throw new EIException("文件保存失败：" + e.getMessage());
        }

        return R.ok().put("file", fileName);
    }

    /**
     * 下载文件
     * 
     * @param fileName 文件名
     * @return 文件内容
     */
    @IgnoreAuth
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String fileName) {
        // 参数校验
        if (StringUtils.isBlank(fileName)) {
            log.warn("文件下载失败：文件名为空");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            // 构建文件路径（兼容 IDE 和 jar包运行）
            File uploadDir = getUploadDir();
            File file = new File(uploadDir, fileName);

            // 文件存在性校验
            if (!file.exists()) {
                log.warn("文件下载失败：文件不存在，路径：{}", file.getAbsolutePath());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // 封装响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // 解决中文文件名乱码问题
            String encodeFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            headers.setContentDispositionFormData("attachment", encodeFileName);

            byte[] fileBytes = FileUtils.readFileToByteArray(file);
            log.info("文件下载成功：{}，大小：{}字节", fileName, fileBytes.length);
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            log.error("文件下载失败：文件名：{}，异常：{}", fileName, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取上传目录（兼容 IDE 和 jar包运行）
     * 
     * @return 上传目录 File 对象
     */
    private File getUploadDir() {
        // 统一使用当前运行目录下的 upload 文件夹
        // WebMvcConfig 也映射到了这个路径
        String uploadPath = System.getProperty("user.dir") + "/upload/";
        return new File(uploadPath);
    }
}
