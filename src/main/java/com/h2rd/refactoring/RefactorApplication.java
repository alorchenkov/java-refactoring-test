package com.h2rd.refactoring;

import com.h2rd.refactoring.usermanagement.User;
import com.h2rd.refactoring.usermanagement.UserOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;

@SpringBootApplication
public class RefactorApplication extends SpringBootServletInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(RefactorApplication.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(RefactorApplication.class);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(RefactorApplication.class);
    }

    @Bean
    public CommandLineRunner loadData(final UserOperations repository) {
        LOG.debug("creating initial data with test users...");

        LOG.debug("drop table user if exists...");
        jdbcTemplate.execute("DROP TABLE USER IF EXISTS");

        LOG.debug("create table user...");
        jdbcTemplate.execute("CREATE TABLE USER(" +
                "EMAIL VARCHAR(255) PRIMARY KEY, NAME VARCHAR(255), ROLES VARCHAR(255))");

        return args -> {
            LOG.debug("add test users...");

            repository.saveUser(new User("Marta Smith", "marta@test.com", Arrays.asList("ADMIN")));
            repository.saveUser(new User("John Goose", "john@test.com", Arrays.asList("USER, GUEST")));

        };
    }
}
