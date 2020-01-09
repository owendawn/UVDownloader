package com.zone.test.base.dbchange;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 2018/5/23 15:09
 *
 * @author owen pan
 */
@Component
public class HikariDSProperty {


    @Value("${spring.datasource.hikari.minimum-idle}")
    public int minimumIdle;
    @Value("${spring.datasource.hikari.maximum-pool-size}")
    public int maximumPoolSize;
    @Value("${spring.datasource.hikari.max-lifetime}")
    public int maxLifetime;
    @Value("${spring.datasource.hikari.connection-timeout}")
    public int connectionTimeout;
    @Value("${spring.datasource.hikari.validation-timeout}")
    public int validationTimeout;
    @Value("${spring.datasource.hikari.idle-timeout}")
    public int idleTimeout;


    @Value("${mybatis.mapperLocations}")
    public String mapperLocations;
    @Value("${mybatis.config-location}")
    public String configLocation;

    public HikariDataSource newDatasource(String dbUrl, String username, String password, String driverClassName){
        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        //configuration
        datasource.setMinimumIdle(this.minimumIdle);
        datasource.setMaximumPoolSize(this.maximumPoolSize);
        datasource.setMaxLifetime(this.maxLifetime);
        datasource.setConnectionTimeout(this.connectionTimeout);
        datasource.setValidationTimeout(this.validationTimeout);
        datasource.setIdleTimeout(this.idleTimeout);
        return datasource;
    }
}
