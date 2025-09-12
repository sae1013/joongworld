package com.example.sw_board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.sw_board.**.mapper")
public class SwBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwBoardApplication.class, args);
	}

}
