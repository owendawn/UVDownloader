package com.zone.test.base.config;
 
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Enumeration;
import java.util.Map;
 
public class WebSocketConfigurator extends ServerEndpointConfig.Configurator {
        public static final String HTTP_SESSION_ID_ATTR_NAME = "HTTP.SESSION.ID";
 
        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
 
                Map<String, Object> attributes = sec.getUserProperties();
                HttpSession session = (HttpSession) request.getHttpSession();
                if (session != null) {
                    attributes.put(HTTP_SESSION_ID_ATTR_NAME, session.getId());
                    Enumeration<String> names = session.getAttributeNames();
                    while (names.hasMoreElements()) {
                        String name = names.nextElement();
                        attributes.put(name, session.getAttribute(name));
                    }
 
                }
        }
    }