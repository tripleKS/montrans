package com.task.rvt.mt.util;

import lombok.Getter;

public class MTransferException extends Exception {
    @Getter
    private int errorCode;

    public MTransferException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public MTransferException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
