package com.igorion.http;

public interface IHttpResponse<T> {

    int getStatusCode();

    T getEntity();

}
