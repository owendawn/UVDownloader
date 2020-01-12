package com.zone.uvdownloader.service;

import com.zone.uvdownloader.entity.M3u8Config;

/**
 * 2020/1/11 14:22
 *
 * @author owen pan
 */
public interface M3u8Service {
    M3u8Config getM3u8ConfigValueByName(String name);

    Long updateM3u8ConfigByName(String name, String value);
}
