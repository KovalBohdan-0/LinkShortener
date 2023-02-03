package com.example.linkshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableCaching(proxyTargetClass = true)
public class LinkShortenerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LinkShortenerApplication.class, args);
    }
}
