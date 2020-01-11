package com.zone.test.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 2020/1/11 16:16
 *
 * @author owen pan
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class M3u8Item {
    private String url;
    private String name;
    private Long length;
    private Double percent=0D;
    private Double during;
}
