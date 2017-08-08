package org.craftercms.social;

import org.craftercms.commons.http.RequestContextBindingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@ImportResource({"classpath:/spring/root-context.xml","classpath:/spring/web-context.xml"})
public class SocialApplicationMain {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SocialApplicationMain.class);
        app.setWebEnvironment(true);
        app.run(args);
    }

    @Bean
    public FilterRegistrationBean requestContextBindingFilter(){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RequestContextBindingFilter());
        registration.addUrlPatterns("/*");
        registration.setName("requestContextBindingFilter");
        registration.setOrder(1);
        return registration;
    }
}
