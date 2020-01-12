package com.zone.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zone.test.base.common.JsonResult;
import com.zone.test.base.config.WebSocketConfigurator;
import com.zone.test.download.m3u8.M3u8Job;
import com.zone.test.entity.WsCommand;
import com.zone.test.util.PFUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.zone.test.controller.M3U8Controller.jobs;


/**
 * @author zhengkai.blog.csdn.net
 */
@ServerEndpoint(value = "/ws/m3u8", configurator = WebSocketConfigurator.class)
@Component
public class M3u8WSServer {
    static Logger log = LoggerFactory.getLogger(M3u8WSServer.class);
    private static ConcurrentHashMap<String, M3u8WSServer> webSocketMap = new ConcurrentHashMap<>();
    private Session session;
    @Autowired
    private M3U8Controller m3U8Controller;
    public static final ObjectMapper objectMapper=new ObjectMapper();

    private String getMac(Session session) {
        return (String) session.getUserProperties().get("ip");
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketMap.put(getMac(session), this);
        System.out.println(getMac(this.session)+"建立连接");
    }

    @OnClose
    public void onClose() {
        webSocketMap.remove(getMac(this.session));
        System.out.println(getMac(this.session)+"断开连接");
    }

    @OnMessage
    public void onMessage(String msg, Session session) {
//        System.out.println(msg);
        WsCommand message= null;
        try {
            message = objectMapper.readValue(msg, WsCommand.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        switch (message.getCommand()){
            case "getJobs":{
                HashMap<String, Object> map = new HashMap<>();
                for (Map.Entry<String, M3u8Job> entry : jobs.entrySet()) {
                    Map m = PFUtil.getFieldMap(entry.getValue());
                    m.remove("items");
                    map.put(entry.getKey(), m);
                }
                sendMessage(new WsCommand().setCommand(message.getCommand()+".res").setData(map));
                break;
            }
            default:{
                sendMessage(new WsCommand().setCommand("error"));
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }


    public void sendMessage(WsCommand message) {
        try {
            this.session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}