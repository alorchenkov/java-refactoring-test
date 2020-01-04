package com.h2rd.refactoring;

import com.h2rd.refactoring.web.UserResource;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
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
        property(ServletProperties.FILTER_FORWARD_ON_404, true);
        configureSwagger();
    }

    private void configureSwagger() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        final BeanConfig config = new BeanConfig();
        config.setTitle("Java Refactoring Test Rest API");
        config.setVersion("v1");
        config.setContact("Alex Lorcencov");
        config.setSchemes(new String[] { "http", "https" });
        config.setBasePath("rest");
        config.setResourcePackage("com.h2rd.refactoring.web");
        config.setPrettyPrint(true);
        config.setScan(true);
    }
}
