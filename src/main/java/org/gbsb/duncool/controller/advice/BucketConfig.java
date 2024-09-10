package org.gbsb.duncool.controller.advice;

import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BucketConfig {
    @Bean
    public Bucket bucket() {

        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(5) // 버킷의 토큰 최대 용량: 5
                        .refillGreedy(5, Duration.ofSeconds(1)) // 1초에 5개의 토큰 리필
                        .initialTokens(5) // 초기 토큰 5개 발급
                )
                .build();
    }
}
