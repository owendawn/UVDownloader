package com.zone.uvdownloader.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zone.uvdownloader.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    List<HashMap<String, Object>> getUser(@Param("id") String id, @Param("name") String name);

    boolean updateUser(@Param("id") String id, @Param("pwd") String pwd);

    List<String> getCount();

    List<String> getCount2();

    List<User> getUser2(@Param("id") String id, @Param("name") String name);
}
