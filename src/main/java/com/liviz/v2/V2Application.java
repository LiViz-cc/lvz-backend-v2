package com.liviz.v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class V2Application {

	public static void main(String[] args) {
		SpringApplication.run(V2Application.class, args);
	}

}
