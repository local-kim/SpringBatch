package com.example.springbatch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Batch에서 Job은 하나의 배치 작업 단위를 얘기하는데요.
 * Job 안에는 아래처럼 여러 Step이 존재하고, Step 안에 Tasklet 혹은 Reader & Processor & Writer 묶음이 존재합니다.
 */
@Slf4j  // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor    // 생성자 DI를 위한 lombok 어노테이션
@Configuration  // Spring Batch의 모든 Job은 @Configuration으로 등록해서 사용합니다.
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;      // 생성자 DI 받음
    private final StepBuilderFactory stepBuilderFactory;    // 생성자 DI 받음

    /**
     * Job : 하나의 배치 작업 단위. Job 안에는 여러 Step이 존재
     */
    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")   // simpleJob 이란 이름의 Batch Job을 생성합니다. job의 이름은 별도로 지정하지 않고, 이렇게 Builder를 통해 지정합니다.
                .start(simpleStep1(null))
                .next(simpleStep2(null))
                .build();
    }

    /**
     * Step : Step 안에 Tasklet 또는 Reader&Processor&Writer 묶음이 존재
     * Tasklet : 개발자가 지정한 커스텀한 기능을 위한 단위
     */
    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep1")    // simpleStep1 이란 이름의 Batch Step을 생성합니다. 마찬가지로 Builder를 통해 이름을 지정합니다.
                .tasklet((contribution, chunkContext) -> {  // Step 안에서 수행될 기능들을 명시합니다. Tasklet은 Step안에서 단일로 수행될 커스텀한 기능들을 선언할 때 사용합니다.
                    log.info(">>>>> This is Step1");    // Batch가 수행되면 log가 출력되도록 합니다.
                    log.info(">>>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is Step2");
                    log.info(">>>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
