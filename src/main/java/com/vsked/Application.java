package com.vsked;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class Application {
	
	public static void main(String[] args) {
		// 解决headless环境问题
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(Application.class, args);
	}
	
}

