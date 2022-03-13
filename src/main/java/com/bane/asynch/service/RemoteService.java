package com.bane.asynch.service;

import com.bane.asynch.domain.Address;
import com.bane.asynch.domain.UserInfo;
import com.bane.asynch.domain.WorkingStatus;
import org.springframework.stereotype.Component;

@Component
public class RemoteService {

    public UserInfo getUserInfo() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new UserInfo(33L, "Mike Jones", 30);
    }

    public WorkingStatus getUserStatus() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new WorkingStatus(WorkingStatus.WORKING_STATUS.WORKING);
    }

    public Address getUserAddress() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Address("Sessame street", 1, 11000);
    }

}
