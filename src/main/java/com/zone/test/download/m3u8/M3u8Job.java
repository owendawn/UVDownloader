package com.zone.test.download.m3u8;

import com.zone.test.entity.M3u8Item;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 2020/1/11 16:34
 *
 * @author owen pan
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class M3u8Job {
    private String from;
    private String dir;
    private String file;
    private Double duringSum;
    private Double length;
    private Double percent = 0D;
    private List<String> msg = new ArrayList<>();
    private List<M3u8Item> items = new ArrayList<>();
}
