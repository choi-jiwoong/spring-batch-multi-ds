package com.sea4.batchtest.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class BatchDataSourceConfig  {

	@Bean(name = "batchDataSource")
	@ConfigurationProperties("spring.datasource.batch")
	public DataSource batchDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "batchTransactionManager")
	public PlatformTransactionManager batchTransactionManager(
		@Qualifier("batchDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}