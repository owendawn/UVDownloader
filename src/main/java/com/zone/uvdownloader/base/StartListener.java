package com.zone.uvdownloader.base;

import com.zone.uvdownloader.controller.M3U8Controller;
import com.zone.uvdownloader.download.JobWorkerOverseer;
import com.zone.uvdownloader.service.M3u8Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Component
public class StartListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger log = LoggerFactory.getLogger(StartListener.class);

    @Value("${server.port}")
    private String port;
    @Autowired
    private M3u8Service m3u8Service;
    @Autowired
    private JobWorkerOverseer jobWorkerOverseer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            System.out.println("项目已启动，可以进行初始化操作");
            M3U8Controller.CONNECT_SIZE.set(Long.parseLong(m3u8Service.getM3u8ConfigValueByName("connect-size").getValue()));
            Executors.newFixedThreadPool(1).submit(jobWorkerOverseer);
        } catch (Exception e) {
            log.error("启动执行失败:" + e.getMessage(), e);
        }
        String url = "http://localhost:" + port;
        System.out.println(">>> 控制台地址 <<< ：" + url);
        try {
//            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        } catch (Exception e) {
            System.out.println("此为无法获取系统默认浏览器");
        }

    }
}