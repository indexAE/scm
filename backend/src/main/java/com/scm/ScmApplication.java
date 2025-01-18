package com.scm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan({"com.scm", "com.example.scm"})
@MapperScan({"com.scm.mapper", "com.example.scm.mapper"})
public class ScmApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScmApplication.class, args);
    }
} 