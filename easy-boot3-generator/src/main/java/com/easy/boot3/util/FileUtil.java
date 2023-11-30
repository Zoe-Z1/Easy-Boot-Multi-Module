package com.easy.boot3.util;

import com.easy.boot3.admin.generate.entity.GenerateCode;
import com.easy.boot3.generator.GenConstant;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zoe
 * @date 2023/11/23
 * @description
 */
public class FileUtil {


    /**
     * 下载zip文件
     * @param response
     * @param codes 需要生成的代码文件
     * @throws IOException
     */
    public static void downloadZip(HttpServletResponse response, List<GenerateCode> codes) throws IOException {
        String filename = codes.get(0).getAuthor() + GenConstant.ZIP_SUFFIX;
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8.name()));
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        OutputStream out = response.getOutputStream();
        // 获取文件名
        ZipOutputStream zip = new ZipOutputStream(out);
        for (GenerateCode code : codes) {
            ZipEntry zipEntry = new ZipEntry(code.getGenPath());
            zip.putNextEntry(zipEntry);
            zip.write(code.getFileContent().getBytes(StandardCharsets.UTF_8));
            zip.flush();
            zip.closeEntry();
        }
        zip.finish();
        zip.close();
    }
}
