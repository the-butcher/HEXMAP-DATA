package com.igorion.http;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;

import com.igorion.fail.impl.C19Failure;
import com.igorion.type.live.ICredentials;

/**
 * definition for types that describe and execute http-requests
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 * @param <T> the type of response exepected when executing the request
 */
public interface IHttpRequest<T> {

    /**
     * get the raw url that this request is pointing to
     * @return
     */
    String getUrlRaw();

    /**
     * set a paramter on this request<br>
     * depending on implementation this parameter will be added to the querystring or to the post-body of the request
     *
     * @param name
     * @param value
     */
    void setParameter(String name, String value);

    /**
     * set a header on this request<br>
     *
     * @param name
     * @param value
     */
    void setHeader(String name, String value);

    /**
     * set an {@link HttpHeaders#AUTHORIZATION} header on this request<br>
     *
     * @param name
     * @param value
     */
    void setAuthorizationHeader(ICredentials credentials);

    /**
     * get a parameter, if set, from the request
     * @param name
     * @return
     */
    Optional<String> optParameter(String name);

    List<NameValuePair> getHttpParameterList();

    /**
     * get the method that will be used when sending this request
     *
     * @return
     */
    ERequestMethod getRequestMethod();

    /**
     * send the request and return the resulting response
     *
     * @return
     * @throws IOException
     */
    IHttpResponse<T> send() throws C19Failure;

    /**
     * subtype specific to GET requests<bR>
     *
     * @author h.fleischer
     * @since 14.03.2020
     *
     * @param <T> the type of response exepected when executing the request
     */
    public static interface GET<T> extends IHttpRequest<T> {

        @Override
        default ERequestMethod getRequestMethod() {
            return ERequestMethod.GET;
        }

        URI getUri();

    }

    /**
     * subtype specific to POST requests<bR>
     *
     * @author h.fleischer
     * @since 14.03.2020
     *
     * @param <T> the type of response exepected when executing the request
     */
    public static interface POST<T> extends IHttpRequest<T> {

        @Override
        default ERequestMethod getRequestMethod() {
            return ERequestMethod.POST;
        }

    }

    /**
     * subtype specific to POST multipart (upload) requests<bR>
     *
     * @author h.fleischer
     * @since 27.03.2020
     *
     * @param <T> the type of response exepected when executing the request
     */
    public static interface POST_MULTIPART<T> extends IHttpRequest<T> {

        void setFile(String name, File file);

        @Override
        default ERequestMethod getRequestMethod() {
            return ERequestMethod.POST;
        }

    }

}
