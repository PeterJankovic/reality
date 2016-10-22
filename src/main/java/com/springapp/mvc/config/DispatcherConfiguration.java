package com.springapp.mvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Import({ThymeleafConfig.class})
public class DispatcherConfiguration extends WebMvcConfigurerAdapter {
}
