package com.zone.uvdownloader.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zone.uvdownloader.base.config.WebSocketConfigurator;
import com.zone.uvdownloader.download.m3u8.M3u8Job;
import com.zone.uvdownloader.entity.WsCommand;
import com.zone.uvdownloader.util.PFUtil;
import org.apache.commons.lang3.StringUtils;
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

import static com.zone.uvdownloader.controller.M3U8Controller.jobs;


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
        webSocketMap.put(session.getId(), this);
        System.out.println("\n"+getMac(this.session)+"【"+session.getId()+"】建立连接");
    }

    @OnClose
    public void onClose() {
        String  mac=getMac(this.session);
        if(webSocketMap.get(session.getId())!=null) {
            webSocketMap.remove(session.getId());
        }
        System.out.println(getMac(this.session)+"【"+session.getId()+"】断开连接");
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
                sendMessage(new WsCommand().setCommand(message.getCommand()+".res").setData(M3U8Controller.getJobStates()));
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