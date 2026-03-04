package com.utils;

import java.io.*;

/**
 * 高性能文件工具类
 */
public class FileUtil {
    
    private static final int BUFFER_SIZE = 8192; // 8KB 缓冲区
    
    public static byte[] FileToByte(File file) throws IOException {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("File too large: " + fileSize);
        }
        
        byte[] result = new byte[(int) fileSize];
        try (InputStream in = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(in, BUFFER_SIZE)) {
            
            int offset = 0;
            int bytesRead;
            while (offset < result.length 
                   && (bytesRead = bis.read(result, offset, result.length - offset)) != -1) {
                offset += bytesRead;
            }
        }
        return result;
    }
    
    /**
     * 高效的文件拷贝方法
     */
    public static void copyFile(File source, File dest) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(source), BUFFER_SIZE);
             OutputStream os = new BufferedOutputStream(new FileOutputStream(dest), BUFFER_SIZE)) {
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }
}
