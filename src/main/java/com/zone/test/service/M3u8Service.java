package com.zone.test.service;

import com.zone.test.entity.M3u8Config;

import java.util.jar.Attributes;

/**
 * 2020/1/11 14:22
 *
 * @author owen pan
 */
public interface M3u8Service {
    M3u8Config getM3u8ConfigValueByName(String name);

    Long updateM3u8ConfigByName(String name, String value);
}
