package com.igorion.http.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.igorion.http.IHttpRequest;
import com.igorion.http.IResponseHandler;

/**
 * implementation of {@link IHttpRequest.POST_MULTIPART}<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 * @param <T> the type of response expected
 */
class HttpRequestPostMultipartmpl<T> extends AHttpRequestImpl<T, HttpPost> implements IHttpRequest.POST_MULTIPART<T> {

    private Map<String, File> files;

    HttpRequestPostMultipartmpl(String urlRaw, IResponseHandler<T> responseHandler) {
        super(urlRaw, responseHandler);
        this.files = new HashMap<>();
    }

    @Override
    public void setFile(String name, File file) {
        this.files.put(name, file);
    }

    @Override
    protected HttpPost createRequest() throws IOException {

        HttpPost request = new HttpPost(getUrlRaw());

        //Creating the MultipartEntityBuilder
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (Entry<String, File> fileEntry : this.files.entrySet()) {
            entityBuilder.addBinaryBody(fileEntry.getKey(), fileEntry.getValue());
        }
        for (NameValuePair nv : getHttpParameterList()) {
            entityBuilder.addTextBody(nv.getName(), nv.getValue());
        }
        getHttpHeaderList().forEach(request::setHeader);
        request.setEntity(entityBuilder.build());

        return request;

    }

}
