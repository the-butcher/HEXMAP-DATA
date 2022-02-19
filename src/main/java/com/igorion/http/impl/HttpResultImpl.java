package com.igorion.http.impl;

import com.igorion.fail.impl.C19Failure;
import com.igorion.http.IHttpResult;

class HttpResultImpl<T> implements IHttpResult<T> {

    private final int statusCode;
    private final T output;
    private final C19Failure failure;

    HttpResultImpl(int statusCode, T output) {
        this.statusCode = statusCode;
        this.output = output;
        this.failure = null;
    }

    HttpResultImpl(int statusCode, C19Failure failure) {
        this.statusCode = statusCode;
        this.output = null;
        this.failure = failure;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public T getOutputEntity() throws C19Failure {
        if (this.failure != null) {
            throw this.failure;
        }
        return this.output;
    }

}
