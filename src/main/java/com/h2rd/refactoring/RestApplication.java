package com.h2rd.refactoring;

import com.h2rd.refactoring.web.UserResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("rest")
public class RestApplication extends ResourceConfig {

    @PostConstruct
    public void init() {
        register(UserResource.class);
    }
}
