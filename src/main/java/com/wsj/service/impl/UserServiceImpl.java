package com.wsj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wsj.common.ErrorCode;
import com.wsj.mapper.UserMapper;
import com.wsj.model.domain.User;
import com.wsj.service.UserService;
import com.wsj.exception.BusinessException;
import com.wsj.utils.AlgorithmActLd;
import com.wsj.utils.JaccardSimilarity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wsj.constant.UserConstant.USER_LOGIN_STATE;
import static com.wsj.utils.MailUtils.sendMail;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 盐值，混淆密码
     */
//    private static final String SALT = "wsj";

    @Override
    public Long userRegister(String userName, String email, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userName, email, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }

        // 账户不能包含特殊字符 TODO 前端校验账户名
//        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
//        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
//        if (matcher.find()) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名中不能包含特殊字符");
//        }
        // 密码和校验密码相同 TODO 前端校验两次密码
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 账户不能重复
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("email", email);
//        long count = userMapper.selectCount(queryWrapper);
//        if (count > 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已注册！");
//        }

        // 2. 加密
//        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUsername(userName);
        user.setEmail(email);
//        user.setUserPassword(encryptPassword);
        user.setUserPassword(userPassword);
        user.setBgImgUrl("http://qn.charon1030.top/project-xunyou/cbc86b4a-29b3-46f5-a1cd-eb7d98e353a4");
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1L;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userEmail, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userEmail, userPassword)) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }

        // 账户不能包含特殊字符
//        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
//        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
//        if (matcher.find()) {
//            return null;
//        }
        // 2. 加密
//        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", userEmail);
        queryWrapper.eq("userPassword", userPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userEmail cannot match userPassword");
            return null;
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 获取验证码
     */
    public String getCheckCode(String mail, String redisKey, String confirmKey, int opt) {
        // opt为1时表示在注册，注册时要验证邮箱是否已注册
        if (opt == 1) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", mail);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已注册！");
            }
        }
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        //生成验证码
        //原来是0-8999，+1000后变成1000-9999
        int ran = (int) (Math.random() * 9000) + 1000;
        String checkCode = String.valueOf(ran);

        sendMail(mail, "您好，欢迎使用寻友，您本次的验证码为：" + checkCode + "。验证码有效期为1分钟，请勿泄露。", "【寻友 by WSJ】账号安全中心");
//            System.out.println("邮箱已发送！");
        try {
            // 指定缓存1分钟过期时间
            valueOperations.set(redisKey, checkCode, 60000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return checkCode;
    }

    @Override
    public int noLoginResetPwd(String email, String userPassword) {
        // 找到邮箱为email的用户
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", email);
        User user = userMapper.selectOne(userQueryWrapper);
        // 重新设置密码
        user.setUserPassword(userPassword);
        // 返回更新结果
        return userMapper.updateById(user);
    }

    /**
     * 用户脱敏
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setBgImgUrl(originUser.getBgImgUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     * 用户注销
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户（内存过滤）
     * @param tagNameList 用户要拥有的标签
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1. 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2. 在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public int updateUser(User user, User loginUser, HttpServletRequest request) {
        // 仅管理员和自己可修改
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 补充校验，如果用户没有传任何要更新的值，就直接报错，不用执行 update 语句
        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新当前（自己的）信息
//        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
//            throw new BusinessException(ErrorCode.NO_AUTH);
//        }
        // 找到当前用户的旧信息
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int isSuccess = userMapper.updateById(user);
        User newUser = userMapper.selectById(userId);
        if (isSuccess > 0) {
            request.getSession().setAttribute(USER_LOGIN_STATE, newUser);
        }
        return isSuccess;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        // 获取当前登录的用户信息
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        //（鉴权）若获取失败，可能是未登录或没有权限，抛异常
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
//            return null;
        }
        return (User) userObj;
    }

    /**
     * 是否为管理员
     */
//    @Override
//    public boolean isAdmin(HttpServletRequest request) {
//        // 仅管理员可查询
//        // 取出角色类型并判断是否为管理员
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User user = (User) userObj;
//        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
//    }

    /**
     * 是否为管理员
     */
//    @Override
//    public boolean isAdmin(User loginUser) {
//        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_ROLE;
//    }

    // TODO 打算要简化一下
    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);

        // 当前用户的tag
        String tags = loginUser.getTags();

        List<Pair<User, Double>> list = new ArrayList<>();

        // 用当前用户的tag跟每一个用户的tag比较
        for (User user : userList) {
            String userTags = user.getTags();
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            Double distance = JaccardSimilarity.calculateJaccardSimilarity(tags, userTags);
            list.add(new Pair<>(user, distance));
        }
        //
        Comparator<Pair<User, Double>> comparator = Comparator.comparing(Pair::getValue);
        comparator = comparator.reversed(); // 从大到小排序
        list.sort(comparator);

//        for (Pair<User, Double> userLongPair : list) {
//            System.out.println("用户id：" + userLongPair.getKey().getId() +
//                    "，用户tags：" + userLongPair.getKey().getTags() +
//                    "，相似度：" + userLongPair.getValue()
//            );
//        }

        List<Long> userIdList = list.stream()
                .map(pair -> pair.getKey().getId())
                .collect(Collectors.toList());

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper).stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getId));

        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }

//        System.out.println(finalUserList);
        return finalUserList;
    }

    public List<User> matchUsers2(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();

//        System.out.println("我的标签：" + tags);

        Gson gson = new Gson();
        /*简单来说，就是把tags中的Json数组转换成一个String类型的List。*/
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();

        // 依次计算所有用户和当前用户的相似度
//        for (int i = 0; i < userList.size(); i++) {
        for (User user : userList) {
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                // 跳过
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());

            // 计算分数
//            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
//            long distance = AlgorithmUtils.minDistance(tags, userTags);
            long distance = AlgorithmActLd.compareUseActLd(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序( 将list集合中的Pair对象按照Long值从小到大排序，并取出前num个Pair对象，存放到topUserPairList集合中 )
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
//        旧的遍历方法
//        for(int i = 0; i < topUserPairList.size(); i++){
//            System.out.println("用户id：" + topUserPairList.get(i).getKey().getId() +
//                    "，用户tags："+ topUserPairList.get(i).getKey().getTags() +
//                    "，相似度："+ topUserPairList.get(i).getValue()
//            );
//        }
//        for (Pair<User, Long> userLongPair : topUserPairList) {
//            System.out.println("用户id：" + userLongPair.getKey().getId() +
//                    "，用户tags：" + userLongPair.getKey().getTags() +
//                    "，编辑距离：" + userLongPair.getValue()
//            );
//        }

        // topUserPairList顺序中的 userId 列表 ( 这段代码的作用是将topUserPairList集合中的Pair对象映射为User对象的id属性，并收集到userIdList集合中 )
        List<Long> userIdList = topUserPairList.stream()
                .map(pair -> pair.getKey().getId())
                .collect(Collectors.toList());

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);

        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        /* 从数据库查出数据后，顺序发生了改变，变成了按id从小到大排序
        将根据userQueryWrapper条件查询得到的User对象集合进行处理和分组，并按照User对象的id属性作为键，
        User对象列表作为值，存放到userIdUserListMap映射中 */
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper).stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getId));


        List<User> finalUserList = new ArrayList<>();
        // 按照userIdList中userId的顺序
        for (Long userId : userIdList) {
            // 将userIdUserListMap中每个userId对应的（第一个）用户添加到finalUserList中。
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }

//        System.out.println(finalUserList);
//        Collections.shuffle(finalUserList);
        return finalUserList;
    }

    @Override
    public long getTotalItems() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return userMapper.selectCount(queryWrapper);
    }

    /**
     * 根据标签搜索用户（SQL 查询版）
     */
//    @Deprecated
//    private List<User> searchUsersByTagsBySQL(List<String> tagNameList) {
//        if (CollectionUtils.isEmpty(tagNameList)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        // 拼接 and 查询
//        // like '%Java%' and like '%Python%'
//        for (String tagName : tagNameList) {
//            queryWrapper = queryWrapper.like("tags", tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
//    }

}




