package com.zone.uvdownloader.entity;

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
    //下载地址
    private String url;
    //名称
    private String name;
    //存放最后路径文件夹名
    private String fileName;
    //下载存放路径
    private String tmpTarget;
    //下载完成存放路径
    private String finishTarget;
    //需下载大小
    private Long length;
    //已完成大小
    private AtomicLong complete=new AtomicLong(0);
    //百分比
    private Double percent=0D;
    //持续时间
    private Double during;
    //0未下载，1正在下载，2下载完成，3下载失败
    private int state=0;
}
