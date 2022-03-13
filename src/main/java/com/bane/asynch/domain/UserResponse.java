package com.bane.asynch.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    @JsonUnwrapped
    private UserInfo userInfo;
    private WorkingStatus workingStatus; 
    private Address address;
}
