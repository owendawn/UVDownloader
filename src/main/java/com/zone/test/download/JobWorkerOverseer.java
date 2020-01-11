package com.zone.test.download;

import org.springframework.stereotype.Component;
import sun.nio.ch.ThreadPool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 2020/1/11 16:45
 *
 * @author owen pan
 */
@Component
public class JobWorkerOverseer implements Runnable {
    public static ConcurrentLinkedQueue<BaseWorker> WORK_POOL = new ConcurrentLinkedQueue<>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(100);

    @Override
    public void run() {
        System.out.println("启动任务监控");
        while (true) {
            if (!WORK_POOL.isEmpty()) {
                BaseWorker it = WORK_POOL.remove();
                if (it != null) {
                    threadPool.submit(it);
                }
            } else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
