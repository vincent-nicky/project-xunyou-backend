package com.wsj.controller;

import cn.hutool.core.io.FileUtil;
import com.wsj.common.BaseResponse;
import com.wsj.common.ErrorCode;
import com.wsj.common.ResultUtils;
import com.wsj.exception.BusinessException;
import com.wsj.model.domain.User;
import com.wsj.service.UserService;
import com.wsj.utils.QiniuImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/userImg")
public class UserImgController {
    @Autowired
    private UserService userService;
    @Resource
    private QiniuImageUtil qiniuImageUtil;

    @PostMapping("/upload/{userId}/{type}")
    public BaseResponse<String> upload(@PathVariable("userId") Long userId,
                                       @PathVariable("type") Long type,
                                       @RequestParam("image") MultipartFile image)
            throws IOException {
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "由于OSS不完善，图片上传功能暂不可用。");
//        // 校验文件
//        long size = image.getSize();
//        String originalFilename = image.getOriginalFilename();
//        // 校验文件大小
//        final long ONE_MB = 3 * 1024 * 1024L;
//        if(size > ONE_MB){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件超过 3MB");
//        }
//        // 校验文件后缀
//        String suffix = FileUtil.getSuffix(originalFilename);
//        final List<String> validFileSuffixList = Arrays.asList("png", "jpg");
//        if(!validFileSuffixList.contains(suffix)){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件后缀非法");
//        }
//
//        // 上传图片
//        String url = qiniuImageUtil.uploadImageQiniu(image);
//        User user = userService.getById(userId);
//        if (type == 1) {
//            user.setAvatarUrl(url);
//        } else {
//            user.setBgImgUrl(url);
//        }
//        userService.updateById(user);
//        return ResultUtils.success(url);
    }

}
