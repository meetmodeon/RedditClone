package com.radish;

import com.radish.config.openApiConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@Import(openApiConfiguration.class)
public class RadishClone1Application {

	public static void main(String[] args) {
		SpringApplication.run(RadishClone1Application.class, args);
	}

}
