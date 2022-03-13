package com.bane.asynch.service;

import com.bane.asynch.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

@Log4j2
@Service
public class UserService {

    private final RemoteService remoteService;

    public UserService(RemoteService remoteService) {
        this.remoteService = remoteService;
    }

    private final User dummyUser = new User(22l, "Mike Jones", 33);

    public User findUser(String id) {
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return dummyUser;
    }

    public UserResponse gatherUserInfoSequential(String userId) throws JsonProcessingException {
        Instant start = Instant.now();

        //1. user info API call
        UserInfo user = remoteService.getUserInfo();

        //2. user working status API call
        WorkingStatus userStatus = remoteService.getUserStatus();

        //3. user address API call
        Address userAddress = remoteService.getUserAddress();

        //combine all API calls results
        UserResponse userResponse = new UserResponse(user, userStatus, userAddress);

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        log.info("All API calls, called in sequence took: {}ms", duration.toMillis());
        return userResponse;
    }

    public UserResponse gatherUserInfoParallel(String userId) throws Exception {
        log.info("gatherUserInfoParallel");
        Instant start = Instant.now();

        //1. user info API call
        Callable<UserInfo> userInfoCallable = new Callable<UserInfo>() {
            @Override
            public UserInfo call() throws Exception {
                Instant childThreadStart = Instant.now();
                UserInfo userInfo = remoteService.getUserInfo();
                Instant childThreadStop = Instant.now();
                Duration duration = Duration.between(childThreadStart, childThreadStop);
                log.info("Child Thread call took: {}ms", duration.toMillis());
                return userInfo;
            }
        };

        //2. user working status API call
        Callable<WorkingStatus> userStatusCallable = remoteService::getUserStatus;

        //3. user address API call
        Callable<Address> userAddressCallable = remoteService::getUserAddress;

        UserInfo userInfo = userInfoCallable.call();
        WorkingStatus workingStatus = userStatusCallable.call();
        Address address = userAddressCallable.call();

        //combine all API calls results
        //UserResponseCallable userResponseCallable = new UserResponseCallable(userInfoCallable, userStatusCallable, userAddressCallable);
        UserResponse userResponse = new UserResponse(userInfo, workingStatus, address);

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        log.info("All API calls, called in sequence took: {}ms", duration.toMillis());
        return userResponse;
    }

    public UserResponse gatherUserInfoThreads(String userId) throws Exception {
        log.info("gatherUserInfoParallel");
        Instant start = Instant.now();

        //1. user info API call
        Callable<UserInfo> userInfoCallable = new Callable<UserInfo>() {
            @Override
            public UserInfo call() throws Exception {
                Instant childThreadStart = Instant.now();
                UserInfo userInfo = remoteService.getUserInfo();
                Instant childThreadStop = Instant.now();
                Duration duration = Duration.between(childThreadStart, childThreadStop);
                log.info("Child Thread UserInfo call took: {}ms", duration.toMillis());
                return userInfo;
            }
        };

        //2. user working status API call
        Callable<WorkingStatus> userStatusCallable = new Callable<WorkingStatus>() {
            @Override
            public WorkingStatus call() throws Exception {
                Instant childThreadStart = Instant.now();
                WorkingStatus userStatus = remoteService.getUserStatus();
                Instant childThreadStop = Instant.now();
                Duration duration = Duration.between(childThreadStart, childThreadStop);
                log.info("Child Thread WorkingStatus call took: {}ms", duration.toMillis());
                return userStatus;
            }
        };

        //3. user address API call
        Callable<Address> userAddressCallable = remoteService::getUserAddress;

        FutureTask<UserInfo> userInfoFutureTask = new FutureTask<>(userInfoCallable);
        FutureTask<WorkingStatus> workingStatusFutureTask = new FutureTask<>(userStatusCallable);
        FutureTask<Address> addressFutureTask = new FutureTask<>(userAddressCallable);

        new Thread(userInfoFutureTask).start();
        new Thread(workingStatusFutureTask).start();
        new Thread(addressFutureTask).start();


        //combine all API calls results
        //UserResponseCallable userResponseCallable = new UserResponseCallable(userInfoCallable, userStatusCallable, userAddressCallable);
        UserResponse userResponse = new UserResponse(userInfoFutureTask.get(), workingStatusFutureTask.get(), addressFutureTask.get());

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        log.info("All API calls, called in sequence took: {}ms", duration.toMillis());
        return userResponse;
    }

}
