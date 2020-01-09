package com.zone.test.base.dbchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 2018/5/23 15:11
 *
 * @author owen pan
 */
@Configuration
@EnableTransactionManagement
public class DSConfig {
    private static final Logger logger = LoggerFactory.getLogger(DSConfig.class);
    @Autowired
    private HikariDSProperty dataSourceProperty;


    @Primary
    @Bean(name = DSNameConsts.MasterDataSource)
    public DataSource masterDataSource(@Value("${spring.datasource.url}")
                                               String dbUrl,
                                       @Value("${spring.datasource.username}")
                                               String username,
                                       @Value("${spring.datasource.password}")
                                               String password,
                                       @Value("${spring.datasource.driver-class-name}")
                                               String driverClassName) {
        DynamicDataSourceHolder.setDefaultDataSource(DSNameConsts.MasterDataSource);
        return dataSourceProperty.newDatasource(dbUrl, username, password, driverClassName);
    }

    @Bean(name = DSNameConsts.ClusterDataSource)
    public DataSource clusterDataSource(@Value("${custom.datasource.ds1.url}")
                                                String dbUrl,
                                        @Value("${custom.datasource.ds1.username}")
                                                String username,
                                        @Value("${custom.datasource.ds1.password}")
                                                String password,
                                        @Value("${custom.datasource.ds1.driver-class-name}")
                                                String driverClassName) {
        DynamicDataSourceHolder.setDefaultDataSource(DSNameConsts.ClusterDataSource);
        return dataSourceProperty.newDatasource(dbUrl, username, password, driverClassName);
    }

    @Bean(name = DSNameConsts.ClusterDataSource2)
    public DataSource clusterDataSource2(@Value("${custom.datasource.ds2.url}")
                                                String dbUrl,
                                        @Value("${custom.datasource.ds2.username}")
                                                String username,
                                        @Value("${custom.datasource.ds2.password}")
                                                String password,
                                        @Value("${custom.datasource.ds2.driver-class-name}")
                                                String driverClassName) {
        DynamicDataSourceHolder.setDefaultDataSource(DSNameConsts.ClusterDataSource2);
        return dataSourceProperty.newDatasource(dbUrl, username, password, driverClassName);
    }
}