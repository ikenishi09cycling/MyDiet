package com.example.app.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.app.interceptor.SessionInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig implements WebMvcConfigurer {
	
	private final SessionInterceptor sessionInterceptor;
	
	/*
	 *  セッションチェック
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(sessionInterceptor).addPathPatterns("/weight/**");
	}

	/*
	 *  バリデーションチェック
	 */
	@Override
	public Validator getValidator() {
		var validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource());
		return validator;
	}
	
	@Bean
	MessageSource messageSource() {
		var messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("validation");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

}
