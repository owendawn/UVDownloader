package com.zone.uvdownloader.download.m3u8;

import com.google.common.util.concurrent.AtomicDouble;
import com.zone.uvdownloader.entity.M3u8Item;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 2020/1/11 16:34
 *
 * @author owen pan
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class M3u8Job {
    private String id;
    //m2u8地址
    private String from;
    //下载路径
    private String dir;
    //文件名
    private String file;
    private Double duringSum;
    private AtomicDouble duringAlready=new AtomicDouble(0D);
    private AtomicLong length=new AtomicLong(0);
    /** 已下载完成的字节数 */
    private AtomicLong complete=new AtomicLong(0);
    /** 已激活线程 */
    private AtomicInteger active=new AtomicInteger(0);
    /** 总切片数 */
    private Integer total;
    /** 已完成切片总数 */
    private AtomicLong count=new AtomicLong(0);
    private List<String> msg = new ArrayList<>();
    private List<M3u8Item> items = new ArrayList<>();
    private AtomicInteger transfered=new AtomicInteger(0);
    private AtomicLong last;
    private AtomicLong speed=new AtomicLong(0);
}
