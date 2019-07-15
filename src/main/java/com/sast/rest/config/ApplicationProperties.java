package com.sast.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "restful", ignoreUnknownFields = false)
public class ApplicationProperties {

}
