package com.sea4.batchtest.config;


import com.sea4.batchtest.user.model.LoginHistoryEntity;
import com.sea4.batchtest.user.service.LoginService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@Slf4j
public class BatchLoginConfig {

	private final LoginService loginService;


	private final PlatformTransactionManager platformTransactionManager;

	@Bean
	public Job loginHistoryResultLogJob(@Qualifier("batchJobRepository") JobRepository jobRepository) {
		return new JobBuilder("loginHistoryResultLogJob", jobRepository)
			.start(loginHistoryResultLogStep(jobRepository))
			.build();
	}

	@Bean
	public Step loginHistoryResultLogStep(@Qualifier("batchJobRepository") JobRepository jobRepository) {
		return new StepBuilder("loginHistoryResultLogStep", jobRepository)
			.<LoginHistoryEntity, LoginHistoryEntity>chunk(10, platformTransactionManager)  // 10건씩 BigQuery로 저장
			.reader(loginHistoryEntityItemReader())
			.writer(loginHistoryEntityItemWriter())
			.build();
	}

	@Bean
	@StepScope
	public ItemReader<LoginHistoryEntity> loginHistoryEntityItemReader() {
		ItemReader<LoginHistoryEntity> reader = loginService.getReader();
		if (reader == null) {
			return new ListItemReader<>(new ArrayList<>());
		}
		return reader;
	}

	@Bean
	public ItemWriter<LoginHistoryEntity> loginHistoryEntityItemWriter() {

		return logs -> {
			log.info("logs size: {}", logs.size());
			loginService.flushBuffer(logs);
		};
	}
}
