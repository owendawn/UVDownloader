package com.zone.test.util;

import java.io.File;

public class PathCheckUtil {

    public static String getPathAvailable(String path){return getPathAvailable(path,null);}
    public static String getPathAvailable(String path,Integer idx){
        String path2;
        if(idx!=null){
            int pi=path.lastIndexOf(".");
            path2=path.substring(0,pi)+"_"+idx+path.substring(pi);
        }else{
            path2=path;
        }
        if(new File(path2).exists()){
            return getPathAvailable(path, (idx==null?0:idx)+1);
        }else {
            return path2;
        }
    }
}
