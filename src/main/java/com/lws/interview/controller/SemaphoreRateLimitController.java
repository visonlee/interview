package com.lws.interview.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Semaphore;

@RestController
@RequestMapping("rate-limit")
@Slf4j
public class SemaphoreRateLimitController {

    private Semaphore semaphore = new Semaphore(3);

    @GetMapping("/hello")
    public String hello() {
        boolean success = semaphore.tryAcquire();
        if (success) {
            log.info("获取锁成功,执行运算");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            semaphore.release();
            return "hello";
        }else {
            log.info("你被限流了, 请稍后再试");
            return "你被限流了, 请稍后再试";
        }
    }

}



