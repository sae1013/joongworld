package com.softworks.joongworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan({
        "com.softworks.joongworld.product.repository"
})
public class JoongWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoongWorldApplication.class, args);
    }

}
