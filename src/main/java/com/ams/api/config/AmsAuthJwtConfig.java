package com.ams.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ams-security")
@Getter
@Setter
public class AmsAuthJwtConfig {

	private JwtConfig jwtConfig;

	@Getter
	@Setter
	public static class JwtConfig {

		private String secret;
		private Long expirationMs;
		private Long refreshExpirationMs;
	}
}
