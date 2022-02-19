package com.igorion.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

import com.igorion.type.json.IJsonType;

/**
 * definition of a type that handles HTTP responses and provides a {@link IJsonType} result
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public interface IResponseHandler<T> extends ResponseHandler<IHttpResult<T>> {

    @Override
    IHttpResult<T> handleResponse(HttpResponse response);

}
