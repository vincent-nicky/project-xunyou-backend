package com.wsj.utils;

import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * 图片上传配置类
 * 注意：静态变量不能直接使用 @ConfigurationProperties 注解进行映射
 */
@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "oss.qiniu")
@Data
public class QiniuImageUtil {

    private String accesskey;      //公钥
    private String secretkey;   //私钥
    private String bucket;   // 存储空间
    private String url;
    private String foldername;

    /**
     * 处理多文件
     *
     * @param multipartFiles
     * @return
     */
    public Map<String, List<String>> uploadImages(MultipartFile[] multipartFiles) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            imageUrls.add(uploadImageQiniu(file));
        }
        map.put("imageUrl", imageUrls);
        return map;
    }

    /**
     * 上传图片到七牛云
     *
     * @param multipartFile
     * @return
     */
    public String uploadImageQiniu(MultipartFile multipartFile) {
        try {
            //1、获取文件上传的流
            byte[] fileBytes = multipartFile.getBytes();

            //2、编辑文件名
            String newImgName = foldername + UUID.randomUUID();

            //3.构造一个带指定 Region 对象的配置类
            //Region.南(根据自己的对象空间的地址选z
            Configuration cfg = new Configuration(Region.regionAs0());
            UploadManager uploadManager = new UploadManager(cfg);

            //4.获取七牛云提供的 token
            Auth auth = Auth.create(accesskey, secretkey);
            String upToken = auth.uploadToken(bucket);
            uploadManager.put(fileBytes, newImgName, upToken);

            //5.返回图片的访问地址
            return url + newImgName;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
