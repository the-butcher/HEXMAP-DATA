package com.igorion.fail.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.igorion.fail.EFailureCode;
import com.igorion.fail.IC19Failure;

public class C19Failure extends RuntimeException implements IC19Failure {

    private static final long serialVersionUID = 7744540588504095252L;

    private final EFailureCode code;

    public C19Failure(EFailureCode code, String message, Throwable cause) {
        super(message, cause);
        if (cause instanceof C19Failure) {
            this.code = ((C19Failure) cause).getCode();
        } else {
            this.code = code;
        }
    }

    @Override
    public String getMessage(int maxDetailCount) {
        return String.format("%s due to %s", getMessage(), Arrays.toString(getDetails(maxDetailCount)));
    }

    @Override
    public String[] getDetails(int maxDetailCount) {
        List<String> detailList = new ArrayList<>();
        Throwable cause = getCause();
        int detailCount = 0;
        while (cause != null && detailCount++ < maxDetailCount) {
            detailList.add(cause.getMessage());
            cause = cause.getCause();
        }
        return detailList.toArray(new String[detailList.size()]);
    }

    @Override
    public EFailureCode getCode() {
        return this.code;
    }

}
