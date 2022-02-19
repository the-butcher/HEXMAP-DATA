package com.igorion.http.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.http.IHttpRequest;
import com.igorion.http.IResponseHandler;

/**
 * implementation of {@link IHttpRequest.GET}<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 * @param <T> the type of response expected
 */
class HttpRequestGetImpl<T> extends AHttpRequestImpl<T, HttpGet> implements IHttpRequest.GET<T> {

    HttpRequestGetImpl(String urlRaw, IResponseHandler<T> responseHandler) {
        super(urlRaw, responseHandler);
    }

    @Override
    public URI getUri() {
        try {
            return new URIBuilder(getUrlRaw()).addParameters(getHttpParameterList()).build();
        } catch (URISyntaxException e) {
            throw new C19Failure(EFailureCode.ILLEGAL_VALUE, "failed to create uri", e);
        }
    }

    @Override
    protected HttpGet createRequest() throws IOException, URISyntaxException {
        HttpGet request = new HttpGet();
        URI uri = new URIBuilder(getUrlRaw()).addParameters(getHttpParameterList()).build();
        getHttpHeaderList().forEach(request::setHeader);
        request.setURI(uri);
        return request;
    }

}
