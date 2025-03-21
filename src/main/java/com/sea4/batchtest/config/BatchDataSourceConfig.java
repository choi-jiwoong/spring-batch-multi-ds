package com.sea4.batchtest.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchDataSourceConfig  {

	@Bean
	@ConfigurationProperties(prefix = "batch.datasource.hikari")
	public HikariConfig hikariConfig() {
		return new HikariConfig();
	}

	@Bean
	public HikariDataSource batchDataSource(HikariConfig hikariConfig) {
		return new HikariDataSource(hikariConfig);
	}

	@Bean
	public PlatformTransactionManager batchTransactionManager(@Qualifier("batchDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@SneakyThrows
	@Bean
	public JobRepository batchJobRepository(
		@Qualifier("batchDataSource") DataSource dataSource,
		@Qualifier("batchTransactionManager") PlatformTransactionManager transactionManager) {

		val factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTransactionManager(transactionManager);
		factory.setDatabaseType("mysql");  // DB 타입 맞게
		factory.setTablePrefix("GCP_BATCH_");  // ✅ 테이블 Prefix 설정
		factory.afterPropertiesSet();      // Spring Batch 5에서 필수 호출
		return factory.getObject();
	}

	@Bean(name="gcpJobLauncher")
	public JobLauncher jobLauncher(JobRepository jobRepository) {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		return jobLauncher;
	}
}