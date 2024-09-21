package com.ams.api.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component(value = "GlobalProperties")
@PropertySource(value = "classpath:global.properties")
@PropertySource(value = "file:/deployments/global/global.properties", ignoreResourceNotFound = true, name = "remoteConfigProps")
@Data
public class GlobalProperties {

	@Value("${global.logging}")
	private boolean globalLog;

	@Value("${global.audit}")
	private boolean globalAudit;

	@Value("${allowed.domains}")
	private String allowedDomains;

	@Value("${global.auditLog}")
	private boolean globalAuditLog;

	@Value("${global.header}")
	private boolean globalHeader;

	@Value("${global.encrypt}")
	private boolean globalEncrypt;

	@Value("${global.redis-cache}")
	private boolean globalRedisCache;

	@Value("${ams-security.jwt-config.secret}")
	private String jwtSecretKey;
}
