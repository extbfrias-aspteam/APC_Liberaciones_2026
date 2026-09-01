package com.mx.asp.pago.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class AuthClientsDbConfig {

	@Value("${auth.datasource.url}")
	private String authDatasourceUrl;

	@Bean(name = "authClientsJdbcTemplate")
	public JdbcTemplate authClientsJdbcTemplate(
			@Value("${auth.datasource.username}") String username,
			@Value("${auth.datasource.password}") String password) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl(authDatasourceUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return new JdbcTemplate(dataSource);
	}
}
