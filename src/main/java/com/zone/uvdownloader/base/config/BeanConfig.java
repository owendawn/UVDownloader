package com.zone.uvdownloader.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Owen Pan on 2017-06-30.
 */
@Configuration
public class BeanConfig {
    @Bean
    public Integer getHelloInteger(){
        return 110;
    }


}
