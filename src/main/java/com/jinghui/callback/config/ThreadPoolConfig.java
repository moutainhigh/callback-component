package com.jinghui.callback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Description: TODO
 * @Author: JingHui Lin
 * @Date: 2020/7/8 10:07
 * @Version V1.0
 */
@Configuration
public class ThreadPoolConfig {

    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(16);
        taskExecutor.setKeepAliveSeconds(5);
        taskExecutor.setMaxPoolSize(32);
        taskExecutor.setQueueCapacity(100);
        return taskExecutor;
    }
}
