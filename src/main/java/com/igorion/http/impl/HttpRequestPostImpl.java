package com.igorion.http.impl;

import java.io.IOException;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import com.igorion.http.IHttpRequest;
import com.igorion.http.IResponseHandler;

/**
 * implementation of {@link IHttpRequest.POST}<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 * @param <T> the type of response expected
 */
class HttpRequestPostImpl<T> extends AHttpRequestImpl<T, HttpPost> implements IHttpRequest.POST<T> {

    HttpRequestPostImpl(final String urlRaw, final IResponseHandler<T> responseHandler) {
        super(urlRaw, responseHandler);
    }

    @Override
    protected HttpPost createRequest() throws IOException {
        HttpPost request = new HttpPost(getUrlRaw());
        request.setEntity(new UrlEncodedFormEntity(getHttpParameterList(), "UTF-8"));
        getHttpHeaderList().forEach(request::setHeader);
        return request;
    }

}
