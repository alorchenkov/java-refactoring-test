package com.h2rd.refactoring;

import com.h2rd.refactoring.web.UserResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("rest")
public class RestApplication extends ResourceConfig {
    private static final Logger LOG = LoggerFactory.getLogger(RestApplication.class);

    @PostConstruct
    public void init() {
        LOG.debug("initializing Jersey context...");
        register(UserResource.class);
    }
}
