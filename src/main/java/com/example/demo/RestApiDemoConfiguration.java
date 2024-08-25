package com.example.demo;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.rest.filter.AuthFilter;

@Configuration
public class RestApiDemoConfiguration {

//	@Bean
//	public FilterRegistrationBean<AuthFilter> loggingFilter(){
//	    FilterRegistrationBean<AuthFilter> registrationBean 
//	      = new FilterRegistrationBean<>();
//	        
//	    registrationBean.setFilter(new AuthFilter());
//	    registrationBean.addUrlPatterns("/v1/*");
//	    registrationBean.setOrder(1);
//	        
//	    return registrationBean;    
//	}
}
