package com.zone.uvdownloader.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.HashMap;

/**
 * 2020/1/12 14:39
 *
 * @author owen pan
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class WsCommand {
    private String command;
    private HashMap data;

    public WsCommand addData(String key ,Object value){
        if(data==null){
            data=new HashMap();
        }
        data.put(key,value);
        return this;
    }
}
