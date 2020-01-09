package com.zone.test.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger log = LoggerFactory.getLogger(StartListener.class);


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            System.out.println("项目已启动，可以进行初始化操作");

        } catch (Exception e) {
            log.error("启动执行失败:" + e.getMessage(), e);
        }
    }

}