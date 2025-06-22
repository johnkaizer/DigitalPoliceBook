package com.kca_2sem_project.digitalob.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/laundrydb}")
    private String jdbcUrl;

    @Value("${spring.datasource.username:root}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);

        // Connection pool settings
        config.setMinimumIdle(5);         // Minimum number of idle connections
        config.setMaximumPoolSize(10);    // Maximum pool size
        config.setAutoCommit(true);       // Auto-commit setting
        config.setIdleTimeout(300000);    // 5 minutes
        config.setMaxLifetime(1200000);   // 20 minutes

        // Transaction isolation level
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");

        return new HikariDataSource(config);
    }
}