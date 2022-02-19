package com.igorion.http.impl;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import com.igorion.http.IOutboundHttpConfig;
import com.igorion.logs.ELogger;

/**
 * imeplementation of {@link IOutboundHttpConfig} responsible for applying timeouts
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
class OutboundHttpConfigImplTimeouts implements IOutboundHttpConfig {

    private final int connectTimeout;
    private final int socketTimeout;

    OutboundHttpConfigImplTimeouts(final int connectTimeout, final int socketTimeout) {
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
    }

    @Override
    public HttpClientBuilder applyTo(HttpClientBuilder builder) {

        ELogger.HTTP.debug(() -> String.format("applying request timeouts to http client builder (connect: %s, read: %s)", this.connectTimeout, this.socketTimeout));

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(this.connectTimeout).setSocketTimeout(this.socketTimeout).build();
        return builder.setDefaultRequestConfig(requestConfig);

    }

}
