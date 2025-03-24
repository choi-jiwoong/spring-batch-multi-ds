package com.sea4.batchtest;

import com.sea4.batchtest.config.BatchDataSourceConfig;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableBatchProcessing
@Import(BatchDataSourceConfig.class)
public class BatchTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchTestApplication.class, args);
	}

}
