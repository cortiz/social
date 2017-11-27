package org.craftercms.social;

import java.util.Arrays;
import java.util.Properties;

import org.craftercms.commons.http.RequestContextBindingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { WebMvcAutoConfiguration.class, ValidationAutoConfiguration.class })
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

    @Bean
    HttpRequestHandler resourceHttpRequestHandler() {
        ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
        resourceHttpRequestHandler.setLocations(Arrays.asList(
            new ClassPathResource("resources/css/"),
            new ClassPathResource("resources/js/"),
            new ClassPathResource("resources/fonts/"),
            new ClassPathResource("resources/image/")
        ));
        return resourceHttpRequestHandler;
    }

    @Bean
    public HandlerMapping fallbackUrlMapping(HttpRequestHandler resourceHttpRequestHandler) {
        UrlFilenameViewController urlFilenameViewController = new UrlFilenameViewController();
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MAX_VALUE - 100);
        Properties urlProperties = new Properties();
        urlProperties.put("/css/**", resourceHttpRequestHandler);
        urlProperties.put("/js/**", resourceHttpRequestHandler);
        urlProperties.put("/fonts/**", resourceHttpRequestHandler);
        urlProperties.put("/image/**", resourceHttpRequestHandler);
        urlProperties.put("/**", urlFilenameViewController);
        mapping.setMappings(urlProperties);
        return mapping;
    }


}
