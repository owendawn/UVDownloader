package com.zone.uvdownloader.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 2020/1/11 14:16
 *
 * @author owen pan
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class M3u8Config {
    private Integer id;
    private String name;
    private String value;
}
