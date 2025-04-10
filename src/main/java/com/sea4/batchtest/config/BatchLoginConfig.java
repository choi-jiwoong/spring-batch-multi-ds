package com.sea4.batchtest.config;


import com.sea4.batchtest.user.model.LoginHistoryEntity;
import com.sea4.batchtest.user.service.LoginService;
import java.util.ArrayList;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchLoginConfig {

	private final LoginService loginService;

	private final PlatformTransactionManager platformTransactionManager;

	private final JobRepository jobRepository;

	private final JobLauncher jobLauncher;

	public BatchLoginConfig(
			LoginService loginService,
			PlatformTransactionManager platformTransactionManager,
			@Qualifier("batchJobRepository") JobRepository jobRepository,
			@Qualifier("batchJobLauncher") JobLauncher jobLauncher) {
		this.loginService = loginService;
		this.platformTransactionManager = platformTransactionManager;
		this.jobRepository = jobRepository;
		this.jobLauncher = jobLauncher;
	}



	@Bean
	public Job loginHistoryResultLogJob() {
		return new JobBuilder("loginHistoryResultLogJob", jobRepository)
			.start(loginHistoryResultLogStep())
			.build();
	}

	@Bean
	public Step loginHistoryResultLogStep() {
		return new StepBuilder("loginHistoryResultLogStep", jobRepository)
			.<LoginHistoryEntity, Future<LoginHistoryEntity>>chunk(10, platformTransactionManager)  // 10건씩 저장
			.reader(loginHistoryEntityItemReader())
			.writer(loginHistoryEntityItemAsyncWriter())
			.build();
	}

	@Bean
	public TaskExecutor loginHistoryTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("login-history-result-log-");
		executor.initialize();
		return executor;
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

	@Bean
	public AsyncItemWriter<LoginHistoryEntity> loginHistoryEntityItemAsyncWriter () {
		AsyncItemWriter<LoginHistoryEntity> asyncWriter = new AsyncItemWriter<>();
		asyncWriter.setDelegate(loginHistoryEntityItemWriter());
		return asyncWriter;
	}
}
