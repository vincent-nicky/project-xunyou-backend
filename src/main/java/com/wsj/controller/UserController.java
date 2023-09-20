package com.wsj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.common.BaseResponse;
import com.wsj.common.ErrorCode;
import com.wsj.common.ResultUtils;
import com.wsj.constant.UserConstant;
import com.wsj.exception.BusinessException;
import com.wsj.model.domain.User;
import com.wsj.model.request.UserLoginRequest;
import com.wsj.model.request.UserRegisterRequest;
import com.wsj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wsj.utils.MailUtils.sendMail;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")

@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletResponse response, HttpServletRequest request) throws IOException {
        // 判断参数是否为空
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验验证码
        codeCheck(userRegisterRequest.getCheckCode(), userRegisterRequest.getEmail());
        // 获取数据
        String userName = userRegisterRequest.getUserName();
        String email = userRegisterRequest.getEmail();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 判断数据是否为空
        if (StringUtils.isAnyBlank(userName, email, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 注册信息
        Long userid = userService.userRegister(userName, email, userPassword, checkPassword);
        // 返回用户的id
        return ResultUtils.success(userid);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 判断参数是否为空
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验验证码
//        if(!loginCodeCheck(userLoginRequest.getCheckCode(), request)){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码错误");
//        }
        // 获取数据
        String userEmail = userLoginRequest.getUserEmail();
        String userPassword = userLoginRequest.getUserPassword();
        // 判断数据是否为空
        if (StringUtils.isAnyBlank(userEmail, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 设置用户的登录态
        User user = userService.userLogin(userEmail, userPassword, request);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }
        // 返回用户信息
        return ResultUtils.success(user);
    }

    /**
     * 用于合法注册邮箱时、更改密码时使用
     */
//    @GetMapping("/sentcheckcode")
    @GetMapping("/code/send")
    public void sentCode(@RequestParam String mail, @RequestParam int opt) {
        // 在缓存中找验证码
        String redisKey = "xunyou:user" + mail + ":confirmKey:";
        String confirmKey = (String) redisTemplate.opsForValue().get(redisKey);
        // 如果在缓存中找不到，将它设置为空字符串
        if (confirmKey == null) {
            confirmKey = "";
            // 生成验证码并在缓存中设置验证码
            userService.getCheckCode(mail, redisKey, confirmKey, opt);
        } else { //如果在缓存中找到了，就再发一遍验证码
            sendMail(mail, "您好，欢迎使用寻友，您本次的验证码为：" + confirmKey + "，请勿泄露。", "【寻友 by WSJ】账号安全中心");
        }
    }

    //    @GetMapping("/resetPwtCheckCode")
    @GetMapping("/code/check")
    public boolean codeCheck(@RequestParam String codeCheck, @RequestParam String email) {
        String redisKey = "xunyou:user" + email + ":confirmKey:";
        String confirmKey = (String) redisTemplate.opsForValue().get(redisKey);
        if (!codeCheck.equals(confirmKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        } else {
            return true;
        }
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
//        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(user);
    }

    @GetMapping("/search/username")
    public BaseResponse<List<User>> searchUsersByUsername(@RequestParam(required = false) String username) {

        // 使用redis
        String redisKey = String.format("xunyou:user:searchbyname:%s", username);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        List<User> userList = (List<User>) valueOperations.get(redisKey);
        if (userList != null) {
            List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
            return ResultUtils.success(list);
        }
        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", username);
        userList = userService.list(queryWrapper);
        // 写缓存
        try {
            // 指定缓存30秒过期时间
            valueOperations.set(redisKey, userList, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }

        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @GetMapping("/search/userid")
    public BaseResponse<List<User>> searchUsersById(@RequestParam(required = false) String userid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userid)) {
            queryWrapper.eq("id", userid);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {

        // 使用redis
        String redisKey = String.format("xunyou:user:searchbytags:%s", tagNameList);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        List<User> userList = (List<User>) valueOperations.get(redisKey);
        if (userList != null) {
            List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
            return ResultUtils.success(list);
        }
        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userList = userService.searchUsersByTags(tagNameList);
        // 写缓存
        try {
            // 指定缓存30秒过期时间
            valueOperations.set(redisKey, userList, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    // todo 推荐多个，未实现
//    @GetMapping("/recommend")
//    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
//        User loginUser = userService.getLoginUser(request);
//        String redisKey = String.format("xunyou:user:recommend:%s", loginUser.getId());
//        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        // 如果有缓存，直接读缓存
//        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
//        if (userPage != null) {
//            return ResultUtils.success(userPage);
//        }
//        // 无缓存，查数据库
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
//        // 写缓存
//        try {
//            // 指定缓存30秒过期时间
//            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
//        } catch (Exception e) {
//            log.error("redis set key error", e);
//        }
//        return ResultUtils.success(userPage);
//    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, @RequestParam int opt, HttpServletRequest request) {
        // BaseResponse<Integer> 泛型要用包装类，不能用int
        // 1.校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 用户在没有登录的时候重置密码
        if (opt == 1) {
            int result = userService.noLoginResetPwd(user.getEmail(), user.getUserPassword());
            return ResultUtils.success(result);
        }

        // 2.校验权限（鉴权）
        User loginUser = userService.getLoginUser(request);
        // 3.触发更新
        int result = userService.updateUser(user, loginUser, request);
        return ResultUtils.success(result);
    }

    /**
     * 获取最匹配的10位用户
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(HttpServletRequest request) {
        // 获取登录者的用户信息
        User user = userService.getLoginUser(request);
        // 返回匹配后的用户列表
        return ResultUtils.success(userService.matchUsers(10, user));
    }

}
