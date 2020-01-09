package com.zone.test.base.dbchange;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 切换数据源Advice
 *
 * @author 单红宇(365384722)
 * @myblog http://blog.csdn.net/catoop/
 * @create 2016年1月23日
 */
@Aspect
@Order(-1)// 保证该AOP在@Transactional之前执行
@Component
public class DataSourceAspect {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);

    @Before("@annotation(ds)")
    public void changeDataSource(JoinPoint point, ToggleDataSource ds) throws Throwable {
        String dsId = ds.value();
        if (!DynamicDataSourceHolder.containsDataSource(dsId)) {
            DynamicDataSourceHolder.setDataSource(DynamicDataSourceHolder.getDefaultDataSource());
        } else {
            DynamicDataSourceHolder.setDataSource(ds.value());
        }
        logger.debug("Use ToggleDataSource : {} > {}", ds.value(), point.getSignature());
    }

    @After("@annotation(ds)")
    public void restoreDataSource(JoinPoint point, ToggleDataSource ds) {
        logger.debug("Revert ToggleDataSource : {} > {}", ds.value(), point.getSignature());
        DynamicDataSourceHolder.clearDataSource();
    }

}