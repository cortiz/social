package org.craftercms.social;

import org.craftercms.commons.http.RequestContextBindingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { WebMvcAutoConfiguration.class })
@PropertySource("classpath:/crafter/api-documentation/api-documentation.properties")
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
