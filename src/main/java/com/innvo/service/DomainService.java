package com.innvo.service;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 
 * @author ali
 *
 */

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="user")
@Component
public class DomainService {
	
	private String domain;

	
	public DomainService() {
		super();
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
