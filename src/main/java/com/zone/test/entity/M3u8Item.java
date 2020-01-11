package com.zone.test.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicLong;

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
    private String fileName;
    private String target;
    private Long length;
    private AtomicLong complete=new AtomicLong(0);
    private Double percent=0D;
    private Double during;
    private boolean finished=false;
}
