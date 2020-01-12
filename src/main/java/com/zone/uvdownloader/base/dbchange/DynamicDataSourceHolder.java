package com.zone.uvdownloader.base.dbchange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owen Pan on 2017-06-16.
 */
public class DynamicDataSourceHolder {
    /**
     * 注意：数据源标识保存在线程变量中，避免多线程操作数据源时互相干扰
     */
    private static final ThreadLocal<String> THREAD_DATA_SOURCE = new ThreadLocal<>();
    private static final ThreadLocal<String> DEFAULT_DATA_SOURCE = new ThreadLocal<>();

    public static List<String> dataSourceIds = new ArrayList<>();

    public static String getDataSource() {
        return THREAD_DATA_SOURCE.get();
    }

    public static String getDefaultDataSource(){
        return DEFAULT_DATA_SOURCE.get();
    }

    public static void setDataSource(String dataSource) {
        THREAD_DATA_SOURCE.set(dataSource);
    }

    public static void clearDataSource() {
        THREAD_DATA_SOURCE.remove();
    }

    public static boolean containsDataSource(String dataSourceId){
        return dataSourceIds.contains(dataSourceId);
    }

    public static void setDefaultDataSource(String dataSourceId){
        if(containsDataSource(dataSourceId)){
            throw new DataSourceAliasRepeatException("the datasource's alias has in it, which is as a default datasource");
        }else{
            dataSourceIds.add(dataSourceId);
            DEFAULT_DATA_SOURCE.set(dataSourceId);
        }
    }

    public static void addDataSource(String dataSourceId){
        if(containsDataSource(dataSourceId)){
            throw new DataSourceAliasRepeatException("the datasource's alias has in it");
        }else{
            dataSourceIds.add(dataSourceId);
        }
    }

    static class DataSourceAliasRepeatException extends RuntimeException{
        public DataSourceAliasRepeatException(String message) {
            super(message);
        }
    }
}