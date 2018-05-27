package com.zone.test;

import com.zone.test.base.dbchange.DynamicDataSourceRegister;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("com.zone.test.mapper")
@Import({DynamicDataSourceRegister.class}) // 注册动态多数据源
@ServletComponentScan
public class SpringbootMvnApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootMvnApplication.class, args);
	}
}
