package com.zone.test.download.m3u8;

import com.zone.test.download.BaseWorker;

/**
 * 2020/1/11 16:45
 *
 * @author owen pan
 */
public class M3u8JobWorker implements BaseWorker {
    private M3u8Job m3u8Job;

    public M3u8JobWorker(M3u8Job m3u8Job) {
        this.m3u8Job = m3u8Job;
    }

    @Override
    public void run() {
        System.out.println("开始下载");
    }
}
