package com.team.updevic001.configuration.config.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsStorage {


   @Value("${cloud.aws.credentials.secret-key}")
   private String SECRET_KEY;

   @Value("${cloud.aws.credentials.access-key}")
   private String ACCESS_KEY;

   @Value("${cloud.aws.region}")
   private String REGION;


   @Bean
   public S3Client s3Client() {
       return S3Client.builder()
               .region(Region.of(REGION))
               .credentialsProvider(() -> AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY))
               .build();
   }


   @Bean
   public S3Presigner s3Presigner() {
       return S3Presigner.builder()
               .region(Region.of(REGION))
               .credentialsProvider(() -> AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY))
               .build();
   }
}
