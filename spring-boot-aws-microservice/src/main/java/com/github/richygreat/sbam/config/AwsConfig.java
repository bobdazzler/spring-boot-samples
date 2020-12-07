package com.github.richygreat.sbam.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.github.richygreat.sbam.properties.CloudAwsCredentialsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.core.region.StaticRegionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class AwsConfig {
    private final Environment environment;

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider(CloudAwsCredentialsProperties cloudAwsCredentialsProperties) {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                cloudAwsCredentialsProperties.getAccessKey(),
                cloudAwsCredentialsProperties.getSecretKey()));
    }

    @Bean
    public StaticRegionProvider staticRegionProvider() {
        return new StaticRegionProvider(environment.getProperty("cloud.aws.region.static",
                Regions.DEFAULT_REGION.getName()));
    }

    @Bean
    public AmazonS3 amazonS3(AWSCredentialsProvider awsCredentialsProvider, StaticRegionProvider staticRegionProvider) {
        return AmazonS3ClientBuilder.standard()
                .withRegion(staticRegionProvider.getRegion().getName())
                .withCredentials(awsCredentialsProvider)
                .build();
    }
}
