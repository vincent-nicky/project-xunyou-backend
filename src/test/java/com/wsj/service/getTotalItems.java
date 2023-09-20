package com.wsj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.mapper.UserMapper;
import com.wsj.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class getTotalItems{
    @Resource
    private UserMapper userMapper;
    @Test
    void test(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        System.out.println(userMapper.selectCount(queryWrapper));
    }
}
