package com.bane.asynch.controller;

import com.bane.asynch.domain.UserResponse;
import com.bane.asynch.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @return UserResponse
     */
    @GetMapping("/user-info-sequential")
    public ResponseEntity<UserResponse> getUserInfoSequential(@RequestParam("userId") String userId) {
        log.info("Single Thread: getUserInfoSequential: " + userId);
        Instant startTomcat = Instant.now();
        UserResponse userResponse = userService.gatherUserInfoSequential(userId);
        Instant endTomcat = Instant.now();
        Duration duration = Duration.between(startTomcat, endTomcat);
        log.info("getUserInfoSequential took: {}ms", duration.toMillis());
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    /**
     * curl "http://localhost:8000/user-info-parallel?userId=pera"
     * <p>
     * Log: Separate Thread per each operation -> parallel execution
     * Complete operation now executed similar to the duration on the longest task: 2002ms and much faster than getUserInfoSequential that took: 4501ms
     * <p>
     * 2022-03-20 18:07:48.603 DEBUG 5850 --- [nio-8000-exec-7] o.s.web.servlet.DispatcherServlet        : GET "/user-info-parallel?userId=pera", parameters={masked}
     * 2022-03-20 18:07:48.603 DEBUG 5850 --- [nio-8000-exec-7] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to com.bane.asynch.controller.UserInfoController#getUserInfoParallel(String)
     * 2022-03-20 18:07:48.605  INFO 5850 --- [nio-8000-exec-7] c.b.a.controller.UserInfoController      : getUserInfoParallel: pera
     * 2022-03-20 18:07:48.605  INFO 5850 --- [nio-8000-exec-7] com.bane.asynch.service.UserService      : gatherUserInfoParallel
     * 2022-03-20 18:07:48.606  INFO 5850 --- [      Thread-28] com.bane.asynch.service.RemoteService    : ...getting userInfo
     * 2022-03-20 18:07:48.606  INFO 5850 --- [      Thread-29] com.bane.asynch.service.RemoteService    : ...getting user status
     * 2022-03-20 18:07:48.607  INFO 5850 --- [      Thread-30] com.bane.asynch.service.RemoteService    : ...getting user address
     * 2022-03-20 18:07:50.106  INFO 5850 --- [      Thread-28] com.bane.asynch.service.UserService      : Child Thread UserInfo call took: 1500ms
     * 2022-03-20 18:07:50.606  INFO 5850 --- [      Thread-29] com.bane.asynch.service.UserService      : Child Thread WorkingStatus call took: 2000ms
     * 2022-03-20 18:07:50.607  INFO 5850 --- [nio-8000-exec-7] com.bane.asynch.service.UserService      : All API calls, called in sequence took: 2001ms
     * 2022-03-20 18:07:50.607  INFO 5850 --- [nio-8000-exec-7] c.b.a.controller.UserInfoController      : getUserInfoParallel took: 2002ms
     * 2022-03-20 18:07:50.608 DEBUG 5850 --- [nio-8000-exec-7] o.s.w.s.m.m.a.HttpEntityMethodProcessor  : Using 'application/json',
     * 2022-03-20 18:07:50.609 DEBUG 5850 --- [nio-8000-exec-7] o.s.w.s.m.m.a.HttpEntityMethodProcessor  : Writing [UserResponse(userInfo=UserInfo(id=33, name=Mike Jones, age=30), workingStatus=WorkingStatus(status=W (truncated)...]
     * 2022-03-20 18:07:50.611 DEBUG 5850 --- [nio-8000-exec-7] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
     * <p/>
     *
     * @param userId
     * @return
     * @throws Exception
     */
    @GetMapping("/user-info-parallel")
    public ResponseEntity<UserResponse> getUserInfoParallel(@RequestParam("userId") String userId) throws Exception {
        log.info("getUserInfoParallel: " + userId);
        Instant startTomcat = Instant.now();
        UserResponse userResponse = userService.gatherUserInfoThreads(userId);
        Instant endTomcat = Instant.now();
        Duration duration = Duration.between(startTomcat, endTomcat);
        log.info("getUserInfoParallel took: {}ms", duration.toMillis());
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
}
