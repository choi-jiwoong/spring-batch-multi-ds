package com.sea4.batchtest.config;


import com.sea4.batchtest.user.model.LoginHistoryEntity;
import com.sea4.batchtest.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {

	private final LoginService loginService;

	private final JobRepository jobRepository;

	private final PlatformTransactionManager platformTransactionManager;

	@Bean
	public Job searchResultLogJob() {
		return new JobBuilder("searchResultLogJob", jobRepository)
			.start(searchResultLogStep())
			.build();
	}

	@Bean
	public Step searchResultLogStep() {
		return new StepBuilder("searchResultLogStep", jobRepository)
			.<LoginHistoryEntity, LoginHistoryEntity>chunk(10, platformTransactionManager)  // 100건씩 BigQuery로 저장
			.reader(searchResultLogReader())
			.writer(searchResultLogWriter())
			.build();
	}

	@Bean
	public ItemReader<LoginHistoryEntity> searchResultLogReader() {
		return loginService.getReader();
	}

	@Bean
	public ItemWriter<LoginHistoryEntity> searchResultLogWriter() {

		return logs -> {
			log.info("logs size: {}", logs.size());
			loginService.flushBuffer(logs);
		};
	}
}
