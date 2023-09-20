package com.wsj.service;
import com.wsj.mapper.UserMapper;
import com.wsj.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Slf4j
@SpringBootTest
public class getUsers {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Test
    void test2() {
//        List<User> list = userMapper.selectList(null);
//        System.out.println(list);

//        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("xunyou:user:recommend:%s", 5001);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        List<User> users = (List<User>) valueOperations.get(redisKey);
        if (users != null) {
            System.out.println(users);
        }else{
            System.out.println("缓存获取失败");
        }

        List<User> list = userService.matchUsers(10,userMapper.selectById(5001));
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        users = userService.page(new Page<>(1, 10), queryWrapper);
        try {
            // 指定缓存30秒过期时间
            valueOperations.set(redisKey, list, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
    }
}
