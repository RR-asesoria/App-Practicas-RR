package org.gestoriarr.appgestoriarr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	@Autowired
	private AuditoriaInterceptor auditoriaInterceptors;
	
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(auditoriaInterceptors)
				.addPathPatterns("/**")
				.excludePathPatterns("/test");
		
	}
	
	
	
}
