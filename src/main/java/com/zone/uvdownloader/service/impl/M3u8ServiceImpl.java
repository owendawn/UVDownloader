package com.zone.uvdownloader.service.impl;

import com.zone.uvdownloader.entity.M3u8Config;
import com.zone.uvdownloader.mapper.M3u8Mapper;
import com.zone.uvdownloader.service.M3u8Service;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 2020/1/11 14:22
 *
 * @author owen pan
 */
@Service
public class M3u8ServiceImpl implements M3u8Service {
    @Resource
    private M3u8Mapper m3u8Mapper;
    @Override
    public M3u8Config getM3u8ConfigValueByName(String name) {
        return m3u8Mapper.getM3u8ConfigValueByName(name);
    }

    @Override
    public Long updateM3u8ConfigByName(String name, String value) {
        return m3u8Mapper.updateM3u8ConfigByName(name,value);
    }
}
