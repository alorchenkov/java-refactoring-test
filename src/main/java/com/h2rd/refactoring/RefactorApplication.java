package com.h2rd.refactoring;

import com.h2rd.refactoring.usermanagement.User;
import com.h2rd.refactoring.usermanagement.UserOperations;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class RefactorApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RefactorApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(RefactorApplication.class);
    }

    @Bean
    public CommandLineRunner loadData(final UserOperations repository) {
        return (args) -> {
            // save some users
            repository.saveUser(new User("Marta Smith", "marta@test.com", Arrays.asList("ADMIN")));
            repository.saveUser(new User("John Goose", "john@test.com", Arrays.asList("USER, GUEST")));

        };
    }
}
