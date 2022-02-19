package com.igorion.http.impl;

import com.igorion.http.IHttpRequest;
import com.igorion.http.IResponseHandler;

/**
 * accessor util to {@link IHttpRequest} instances<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public class HttpRequest {

    private HttpRequest() {
        //no public instance
    }

    /**
     * subtype for GET requests
     *
     * @author h.fleischer
     * @since 14.03.2020
     *
     */
    public static final class GET {

        private GET() {
            //no public instance
        }

        public static <T> IHttpRequest.GET<T> create(String urlRaw, IResponseHandler<T> responseHandler) {
            return new HttpRequestGetImpl<>(urlRaw, responseHandler);
        }

    }

    /**
     * subtype for POST requests
     *
     * @author h.fleischer
     * @since 14.03.2020
     *
     */
    public static final class POST {

        private POST() {
            //no public instance
        }

        public static <T> IHttpRequest.POST<T> create(String urlRaw, IResponseHandler<T> responseHandler) {
            return new HttpRequestPostImpl<>(urlRaw, responseHandler);
        }

    }

    /**
     * subtype for POST requests
     *
     * @author h.fleischer
     * @since 14.03.2020
     *
     */
    public static final class POST_MULTIPART {

        private POST_MULTIPART() {
            //no public instance
        }

        public static <T> IHttpRequest.POST_MULTIPART<T> create(String urlRaw, IResponseHandler<T> responseHandler) {
            return new HttpRequestPostMultipartmpl<>(urlRaw, responseHandler);
        }

    }

}
