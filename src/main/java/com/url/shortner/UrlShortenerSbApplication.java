package com.url.shortner;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class UrlShortenerSbApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShortenerSbApplication.class, args);
	}

}
