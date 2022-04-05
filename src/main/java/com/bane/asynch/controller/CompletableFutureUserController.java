package com.bane.asynch.controller;

import com.bane.asynch.domain.UserInfo;
import com.bane.asynch.domain.UserResponse;
import com.bane.asynch.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Log4j2
@RestController
public class CompletableFutureUserController {

    private final UserService userService;
    private final TaskExecutor taskExecutor;

    public CompletableFutureUserController(UserService userService, TaskExecutor taskExecutor) {
        this.userService = userService;
        this.taskExecutor = taskExecutor;
    }

    @GetMapping(value = "/user-using-completable-future")
    public CompletableFuture<UserResponse> getUserInfoUsingCompletableFuture(@RequestParam("userId") String userId) {
        log.info("getUserInfoUsingCompletableFuture: " + userId);
        Instant startTomcat = Instant.now();


        CompletableFuture<UserResponse> userInfoCompletableFuture = new CompletableFuture<>();
        CompletableFuture<UserResponse> userInfo = userInfoCompletableFuture.completeAsync(new Supplier<UserResponse>() {
            @Override
            public UserResponse get() {
                return userService.gatherUserInfoSequential(userId);

            }
        });

        Instant endTomcat = Instant.now();
        Duration duration = Duration.between(startTomcat, endTomcat);
        log.info("getUserInfoUsingCompletableFuture took: {}ms", duration.toMillis());
        return userInfo;
    }
}
