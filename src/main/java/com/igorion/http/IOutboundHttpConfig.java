package com.igorion.http;

import org.apache.http.impl.client.HttpClientBuilder;

/**
 * definition for types that can apply a setting to a {@link HttpClientBuilder} instances<br>
 * this is usually done right before issuing a http request<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
@FunctionalInterface
public interface IOutboundHttpConfig {

    /**
     * apply something to the given {@link HttpClientBuilder} instance, then return the altered instance
     *
     * @param builder
     * @return
     */
    HttpClientBuilder applyTo(HttpClientBuilder builder);

}
