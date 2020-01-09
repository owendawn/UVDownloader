package com.zone.test.base.dbchange;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 2018/12/3 14:41
 *
 * @author owen pan
 */
@Configuration
public class DataSourceConfig {
    @Autowired
    @Qualifier(DSNameConsts.MasterDataSource)
    private DataSource masterDataSource;
    @Autowired
    @Qualifier(DSNameConsts.ClusterDataSource)
    private DataSource clusterDataSource;
    @Autowired
    @Qualifier(DSNameConsts.ClusterDataSource2)
    private DataSource clusterDataSource2;
    /**
     * 核心动态数据源
     *
     * @return 数据源实例
     */
    @Bean
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setDefaultTargetDataSource(masterDataSource);
        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put(DSNameConsts.MasterDataSource,masterDataSource);
        dataSourceMap.put(DSNameConsts.ClusterDataSource,clusterDataSource);
        dataSourceMap.put(DSNameConsts.ClusterDataSource2,clusterDataSource2);
        dataSource.setTargetDataSources(dataSourceMap);
        return dataSource;
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }


    @Value("${mybatis.mapperLocations}")
    private String mapperLocations;
    @Value("${mybatis.config-location}")
    private String configLocation;
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        //mybatis plus 定制
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        //mybatis 原装
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        //此处设置为了解决找不到mapper文件的问题
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource(configLocation));

        sqlSessionFactoryBean.setPlugins(new Interceptor[]{ //PerformanceInterceptor(),OptimisticLockerInterceptor()
                paginationInterceptor() //添加分页功能
        });
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory());
    }

    /**
     * 事务管理
     *
     * @return 事务管理实例
     */
    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new DataSourceTransactionManager(dynamicDataSource());
    }

}
