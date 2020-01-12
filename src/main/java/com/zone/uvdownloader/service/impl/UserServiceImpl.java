package com.zone.uvdownloader.service.impl;

import java.util.HashMap;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zone.uvdownloader.base.common.JsonPageResult;
import com.zone.uvdownloader.base.dbchange.DSNameConsts;
import com.zone.uvdownloader.base.dbchange.ToggleDataSource;
import com.zone.uvdownloader.entity.User;
import com.zone.uvdownloader.mapper.UserMapper;
import com.zone.uvdownloader.base.common.RollBackException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.zone.uvdownloader.service.UserService;

import javax.annotation.Resource;

@Service(value = "userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public List<HashMap<String, Object>> getUser(String name) {
        try {
            return userMapper.getUser("2", name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean testTransactional(String pwd) {
        boolean b = userMapper.updateUser("2", pwd);
        System.out.println(b);
        List<?> l = null;
        if (true) {
            throw new RollBackException("测试事务回滚");
        }
        return false;
    }

//    @ToggleDataSource(DSNameConsts.ClusterDataSource)
    @Override
    public List<String> getCount() {
        return userMapper.getCount();
    }

    @ToggleDataSource(DSNameConsts.MasterDataSource)
    @Override
    public List<User> getCount2() {
        return getBaseMapper().selectList(new QueryWrapper<User>().isNotNull("id"));
    }

    @Override
    public IPage<User> queryUser(Page<User> userPage) {
        return getBaseMapper().selectPage(userPage, new QueryWrapper<>());
    }

    @Override
    public JsonPageResult<List<User>> queryUser2(int i, int i1) {
        com.github.pagehelper.Page<User> page = PageHelper.startPage(i, i1);
        List<User> users = userMapper.getUser2(null, null);
        return new JsonPageResult.Builder<List<User>>().page(new JsonPageResult.Page().pageSize(page.getPageSize()).pageNum(page.getPageNum()).total(page.getTotal())).data(users).build();
    }

}
