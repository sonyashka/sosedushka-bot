package com.ppobot.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DbConfig extends DefaultDbConfig {

    @Bean
    @Qualifier("bot-db")
    @ConfigurationProperties(prefix = "app.db.bot-db")
    SpringDataJdbcProperties gitlabJdbcProperties() {
        return new SpringDataJdbcProperties();
    }

    @Bean
    @Qualifier("bot-db")
    public DataSource gitlabDataSource(@Qualifier("bot-db") SpringDataJdbcProperties properties) {
        return hikariDataSource("db", properties);
    }

    @Bean
    @Qualifier("bot-db")
    JdbcTemplate gitlabJdbcTemplate(@Qualifier("bot-db") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Data
    @NoArgsConstructor
    public static class SpringDataJdbcProperties {

        // constants
//        private static final String H2_DATABASE_DRIVER = "org.h2.Driver";
        String url;
        String driver;
        String user;
        String password;
        String poolSize;
        int minPoolSize = 4;
        int maxPoolSize = 10;
        long idleTimeout;
        long maxLifetime;
        Integer bulkSize;

        public SpringDataJdbcProperties(
                String url, String driver, String user, String password, String poolSize, Integer bulkSize) {
            this.url = url;
            this.driver = driver;
            this.user = user;
            this.password = password;
            this.poolSize = poolSize;
            this.bulkSize = bulkSize;
        }

        @Override
        public String toString() {
            var props = new SpringDataJdbcProperties(
                    url, driver, user, ((password == null) || password.isEmpty()) ? "" : "*****", poolSize, bulkSize);
            return Json.encode(props);
        }

    }

}
