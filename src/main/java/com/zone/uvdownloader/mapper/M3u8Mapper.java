package com.zone.uvdownloader.mapper;

import com.zone.uvdownloader.entity.M3u8Config;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 2020/1/11 14:22
 *
 * @author owen pan
 */
@Mapper
public interface M3u8Mapper {
    @Select("select * from m3u8_config where name=#{name}")
    M3u8Config getM3u8ConfigValueByName(@Param("name") String name);

    @Update("update m3u8_config set value=#{value} where name=#{name}")
    Long updateM3u8ConfigByName(@Param("name") String name, @Param("value") String value);
}
