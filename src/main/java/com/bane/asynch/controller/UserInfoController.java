package com.bane.asynch.controller;

import com.bane.asynch.domain.UserResponse;
import com.bane.asynch.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@Log4j2
@RestController
public class UserInfoController {

    private final UserService userService;

    public UserInfoController(UserService userService) {
        this.userService = userService;
    }


    /**
     * curl "http://localhost:8000/user-info-sequential?userId=22"
     */
    @GetMapping("/user-info-sequential")
    public UserResponse getUserInfoSequential(@RequestParam("userId") String userId) {
        log.info("getUserInfoSequential: " + userId);
        Instant startTomcat = Instant.now();
        UserResponse userResponse = userService.gatherUserInfoSequential(userId);
        Instant endTomcat = Instant.now();
        Duration duration = Duration.between(startTomcat, endTomcat);
        log.info("getUserInfoSequential took: {}ms", duration.toMillis());
        return userResponse;
    }

    /**
     * curl "http://localhost:8000/user-info-parallel?userId=pera"
     *
     * @param userId
     * @return
     * @throws Exception
     */
    @GetMapping("/user-info-parallel")
    public UserResponse getUserInfoParallel(@RequestParam("userId") String userId) throws Exception {
        log.info("getUserInfoParallel: " + userId);
        Instant startTomcat = Instant.now();
        UserResponse userResponse = userService.gatherUserInfoThreads(userId);
        Instant endTomcat = Instant.now();
        Duration duration = Duration.between(startTomcat, endTomcat);
        log.info("getUserInfoParallel took: {}ms", duration.toMillis());
        return userResponse;
    }
}
