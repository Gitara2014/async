package com.bane.asynch.util;

import com.bane.asynch.domain.UserInfo;
import com.bane.asynch.service.RemoteService;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

@Log4j2
public class CallableDemo {

    public static void main(String[] args) {
        log.info(Thread.currentThread().getName());
        RemoteService remoteService = new RemoteService();

        Callable<UserInfo> userInfoCallable = () -> {
            log.info(Thread.currentThread().getName());
            Instant start = Instant.now();
            UserInfo userInfo = remoteService.getUserInfo();
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            log.info("Task took: {}ms", duration.toMillis());
            return userInfo;
        };

        try {
            UserInfo call = userInfoCallable.call();
            log.info(call.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
