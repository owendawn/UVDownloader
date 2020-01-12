package com.zone.uvdownloader.base.dbchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Created by Owen Pan on 2017-06-16.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        // 从自定义的位置获取数据源标识
        return DynamicDataSourceHolder.getDataSource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection=determineTargetDataSource().getConnection();
        DatabaseMetaData md=connection.getMetaData();
        logger.debug("使用数据源："+md.getURL());
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        return determineTargetDataSource().getConnection();
    }
}