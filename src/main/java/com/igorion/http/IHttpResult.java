package com.igorion.http;

import java.io.IOException;

import com.igorion.fail.impl.C19Failure;
import com.igorion.fail.impl.OutboundRequestFailure;

/**
 * definition of a type that holds the result of an {@link IHttpRequest}<br>
 * this type gives the opportunity to cross the line between the http {@link IOException}s and the app internal {@link C19Failure}s<br>
 * the response can be parsed and errors evaluated<br>
 * only when the response is fetched, any error present is thrown<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public interface IHttpResult<T> {

    int getStatusCode();

    /**
     * get the result of this response<br>
     *
     * @return
     * @throws OutboundRequestFailure any error that may have occurred during response handling
     */
    T getOutputEntity() throws C19Failure;

}
