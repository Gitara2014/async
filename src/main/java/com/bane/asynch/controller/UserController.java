package com.bane.asynch.controller;

import com.bane.asynch.domain.User;
import com.bane.asynch.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

@Log4j2
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * $ curl "http://localhost:8000/user-sync?id=22"
     *
     * <p>
     * Synchronous call - single thread
     * <p>
     * <p>
     * 2022-03-13 19:04:53.287 DEBUG 71423 --- [nio-8000-exec-8] o.s.web.servlet.DispatcherServlet        : GET "/user-sync?id=22", parameters={masked}
     * 2022-03-13 19:04:53.288 DEBUG 71423 --- [nio-8000-exec-8] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to com.bane.asynch.controller.UserController#getUserSynchronous(String)
     * 2022-03-13 19:04:53.288  INFO 71423 --- [nio-8000-exec-8] c.bane.asynch.controller.UserController  : UserId: 22
     * 2022-03-13 19:04:56.289  INFO 71423 --- [nio-8000-exec-8] c.bane.asynch.controller.UserController  : Call took: 3000ms
     * 2022-03-13 19:04:56.289 DEBUG 71423 --- [nio-8000-exec-8] m.m.a.RequestResponseBodyMethodProcessor : Using 'application/json',given . . .
     * 2022-03-13 19:04:56.290 DEBUG 71423 --- [nio-8000-exec-8] m.m.a.RequestResponseBodyMethodProcessor : Writing [User(id=22, name=Mike Jones, age=33)]
     * 2022-03-13 19:04:56.291 DEBUG 71423 --- [nio-8000-exec-8] o.s.web.servlet.DispatcherServlet        : Completed 200 OK
     *
     * @param id User's id
     * @return Found user
     */
    @GetMapping("/user-sync")
    public User getUserSynchronous(@RequestParam String id) {
        log.info("UserId: " + id);

        Instant startTomcat = Instant.now();

        User user = userService.findUser(id);

        Instant endTomcat = Instant.now();
        Duration duration = Duration.between(startTomcat, endTomcat);
        log.info("Call took: {}ms", duration.toMillis());

        return user;
    }

    /**
     * $ curl http://localhost:8000/user-async?id=44
     *
     * <p>
     * Asynchronous call - 3 threads
     *
     * <p>
     * Thread one: Tomcat thread
     * Calls take 1ms and then thread is released to receive other requests from the users.
     * Log: DispatcherServlet        : Exiting but response remains open for further handling
     * </p>
     * <p>
     * Thread two: Callback thread
     * A child thread is created to invoke the business method in the backend server.
     * Will execute asynchronously and return the result to the web server independently.
     * </p>
     *
     * <p>
     * Thread three:
     * Resume with async result handling
     * Log: DispatcherServlet       : "ASYNC" dispatch for GET "/user-async?id=44", parameters={masked}
     * </p>
     *
     * <p>
     * 2022-03-13 19:06:12.826 DEBUG 71423 --- [io-8000-exec-10] o.s.web.servlet.DispatcherServlet        : GET "/user-async?id=22", parameters={masked}
     * 2022-03-13 19:06:12.826 DEBUG 71423 --- [io-8000-exec-10] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped to com.bane.asynch.controller.UserController#getUserAsynchronous(String)
     * 2022-03-13 19:06:12.827  INFO 71423 --- [io-8000-exec-10] c.bane.asynch.controller.UserController  : UserId: 22
     * 2022-03-13 19:06:12.827  INFO 71423 --- [io-8000-exec-10] c.bane.asynch.controller.UserController  : Call took: 0ms
     * 2022-03-13 19:06:12.827 DEBUG 71423 --- [io-8000-exec-10] o.s.w.c.request.async.WebAsyncManager    : Started async request
     * 2022-03-13 19:06:12.828 DEBUG 71423 --- [io-8000-exec-10] o.s.web.servlet.DispatcherServlet        : Exiting but response remains open for further handling
     * 2022-03-13 19:06:15.828  INFO 71423 --- [         task-3] c.bane.asynch.controller.UserController  : Child Thread call took: 3000ms
     * 2022-03-13 19:06:15.829 DEBUG 71423 --- [         task-3] o.s.w.c.request.async.WebAsyncManager    : Async result set, dispatch to /user-async
     * 2022-03-13 19:06:15.830 DEBUG 71423 --- [nio-8000-exec-1] o.s.web.servlet.DispatcherServlet        : "ASYNC" dispatch for GET "/user-async?id=22", parameters={masked}
     * 2022-03-13 19:06:15.831 DEBUG 71423 --- [nio-8000-exec-1] s.w.s.m.m.a.RequestMappingHandlerAdapter : Resume with async result [User(id=22, name=Mike Jones, age=33)]
     * 2022-03-13 19:06:15.831 DEBUG 71423 --- [nio-8000-exec-1] m.m.a.RequestResponseBodyMethodProcessor : Using 'application/json'
     * 2022-03-13 19:06:15.832 DEBUG 71423 --- [nio-8000-exec-1] m.m.a.RequestResponseBodyMethodProcessor : Writing [User(id=22, name=Mike Jones, age=33)]
     * 2022-03-13 19:06:15.833 DEBUG 71423 --- [nio-8000-exec-1] o.s.web.servlet.DispatcherServlet        : Exiting from "ASYNC" dispatch, status 200
     * </p>
     *
     * @param id User's id
     * @return found User
     */
    @GetMapping("/user-async")
    public Callable<User> getUserAsynchronous(@RequestParam String id) {
        log.info("UserId: " + id);
        Instant startTomcat = Instant.now();

        Callable<User> userCallable = () -> {
            Instant childThreadStart = Instant.now();
            User user = userService.findUser(id);
            Instant childThreadStop = Instant.now();
            Duration duration = Duration.between(childThreadStart, childThreadStop);
            log.info("Child Thread call took: {}ms", duration.toMillis());
            return user;
        };

        Instant endTomcat = Instant.now();
        Duration duration = Duration.between(startTomcat, endTomcat);
        log.info("Call took: {}ms", duration.toMillis());
        return userCallable;
    }

}
