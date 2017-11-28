package org.craftercms.social.management;

import org.craftercms.commons.http.RequestContextBindingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
@ImportResource({"classpath:/crafter/profile/client-context.xml",
                 "classpath:/crafter/security/security-context.xml",
                "classpath:/web-context.xml"})
@Configuration
@EnableAutoConfiguration(exclude = { WebMvcAutoConfiguration.class })
public class SocialAdminApplicationMain extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SocialAdminApplicationMain.class);
        app.setWebEnvironment(true);
        app.run(args);
    }

    @Bean
    @Primary
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
        List<Resource> resources = Arrays.asList(
            new ClassPathResource("resources/css/"),
            new ClassPathResource("resources/js/"),
            new ClassPathResource("resources/fonts/"),
            new ClassPathResource("resources/image/"),
            new ClassPathResource("resources/")
        );
        resourceHttpRequestHandler.setLocations(resources);
        return resourceHttpRequestHandler;
    }

    @Bean
    @Primary
    public SimpleUrlHandlerMapping fallbackUrlMapping(final ResourceHttpRequestHandler resourceHttpRequestHandler) {
        UrlFilenameViewController urlFilenameViewController = new UrlFilenameViewController();
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MAX_VALUE - 100);
        Properties urlProperties = new Properties();
        urlProperties.put("/css/**", resourceHttpRequestHandler);
        urlProperties.put("/js/**", resourceHttpRequestHandler);
        urlProperties.put("/fonts/**", resourceHttpRequestHandler);
        urlProperties.put("/image/**", resourceHttpRequestHandler);
        urlProperties.put("/resources/**", resourceHttpRequestHandler);
        urlProperties.put("/**", urlFilenameViewController);
        mapping.setMappings(urlProperties);
        return mapping;
    }
}
