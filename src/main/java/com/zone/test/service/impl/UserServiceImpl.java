package com.zone.test.service.impl;

import java.util.HashMap;
import java.util.List;

import com.zone.test.base.common.RollBackException;
import com.zone.test.base.dbchange.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.zone.test.mapper.UserMapper;
import com.zone.test.service.UserService;

@Service(value = "userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public List<HashMap<String, Object>> checkUserExists(String name) {
        try {
            return userMapper.checkUserExists(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public boolean testTransactional(String pwd) {
        boolean b = userMapper.updateUser(pwd);
        System.out.println(b);
        List<?> l = null;
        if (l == null) {
            throw new RollBackException("测试事务回滚");
        }
        return false;
    }


    @Override
    @DataSource("ds1")
    public Integer getCount() {
        return userMapper.getCount();
    }

}
