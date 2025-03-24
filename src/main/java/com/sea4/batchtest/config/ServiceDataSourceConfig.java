package com.sea4.batchtest.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Primary
public class ServiceDataSourceConfig {

	@Primary
	@Bean(name = "dataSource")
	@ConfigurationProperties(prefix = "spring.datasource.service")
	public DataSource serviceDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager serviceTransactionManager(
		@Qualifier("dataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}