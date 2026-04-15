package com.rungo.api.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {

        // 기본 설정 SimpleAsyncTaskExecutor -> Thread 무제한 생성 가능해 OOM 발생 가능
        // 직접 설정하여 OOM 방지(MVP 단계로 최소한으로 생성)
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 항상 대기하는 인원
        executor.setMaxPoolSize(5); // 대기열 꽉 차면 추가로 투입하는 인원
        executor.setQueueCapacity(50); // 작업 대기공간
        executor.setThreadNamePrefix("Mail-Async-"); // 로그 확인용 접두사
        executor.initialize();
        return executor;
    }
}