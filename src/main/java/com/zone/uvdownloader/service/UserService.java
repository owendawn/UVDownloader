package com.zone.uvdownloader.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zone.uvdownloader.base.common.JsonPageResult;
import com.zone.uvdownloader.entity.User;

import java.util.HashMap;
import java.util.List;

public interface UserService {
    List<HashMap<String, Object>> getUser(String name);

    boolean testTransactional(String pwd);

    List<String> getCount();

    List<User> getCount2();

    IPage<User> queryUser(Page<User> userPage);

    JsonPageResult<List<User>> queryUser2(int i, int i1);
}
