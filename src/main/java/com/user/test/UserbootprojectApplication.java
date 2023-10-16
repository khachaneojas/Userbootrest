package com.user.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
//@ComponentScan(basePackages = "com.user.test")
@EnableEurekaClient
public class UserbootprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserbootprojectApplication.class, args);
	}

}
