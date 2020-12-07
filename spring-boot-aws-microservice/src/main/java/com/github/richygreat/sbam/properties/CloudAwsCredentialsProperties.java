package com.github.richygreat.sbam.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("cloud.aws.credentials")
public class CloudAwsCredentialsProperties {
    private String accessKey;
    private String secretKey;
}
