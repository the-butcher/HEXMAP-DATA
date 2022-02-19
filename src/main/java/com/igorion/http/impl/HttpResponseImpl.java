package com.igorion.http.impl;

import com.igorion.http.IHttpResponse;

class HttpResponseImpl<T> implements IHttpResponse<T> {

    private final int statusCode;
    private final T entity;

    HttpResponseImpl(int statusCode, T entity) {
        this.statusCode = statusCode;
        this.entity = entity;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public T getEntity() {
        return this.entity;
    }

}
