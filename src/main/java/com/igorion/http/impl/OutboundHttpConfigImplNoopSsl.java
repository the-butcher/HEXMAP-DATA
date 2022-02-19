package com.igorion.http.impl;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import com.igorion.http.IOutboundHttpConfig;
import com.igorion.logs.ELogger;

class OutboundHttpConfigImplNoopSsl implements IOutboundHttpConfig {

    OutboundHttpConfigImplNoopSsl() {

    }

    @Override
    public HttpClientBuilder applyTo(HttpClientBuilder builder) {

        ELogger.HTTP.debug(() -> "applying no-op ssl handling to http client builder");

        try {
            SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
            SSLConnectionSocketFactory noopSsl = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            return builder.setSSLSocketFactory(noopSsl);
        } catch (Exception generalFailure) {
            ELogger.HTTP.warn(() -> "failed to apply no-op ssl handling to http client builder", generalFailure);
            return builder;
        }

    }

}
