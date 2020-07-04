package com.nmt.education.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "nmt.token")
@Getter
@Setter
@Component
public class TokenConfigProperties {
    private String key;
    private int expireMinute;

}
