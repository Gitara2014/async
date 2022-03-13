package com.bane.asynch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkingStatus {
    public enum WORKING_STATUS {
        WORKING,
        NON_WORKING
    }

    private WORKING_STATUS status;
}
