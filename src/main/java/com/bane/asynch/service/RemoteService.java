package com.bane.asynch.service;

import com.bane.asynch.domain.Address;
import com.bane.asynch.domain.UserInfo;
import com.bane.asynch.domain.WorkingStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RemoteService {

    public UserInfo getUserInfo() {
        try {
            log.info("...getting userInfo");
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new UserInfo(33L, "Mike Jones", 30);
    }

    public WorkingStatus getUserStatus() {
        try {
            log.info("...getting user status");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new WorkingStatus(WorkingStatus.WORKING_STATUS.WORKING);
    }

    public Address getUserAddress() {
        try {
            log.info("...getting user address");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Address("Sesame street", 1, 11000);
    }

}
