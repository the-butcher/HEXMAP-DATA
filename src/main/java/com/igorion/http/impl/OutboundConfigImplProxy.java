package com.igorion.http.impl;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;

import com.igorion.http.IOutboundFtpConfig;
import com.igorion.http.IOutboundHttpConfig;
import com.igorion.logs.ELogger;

/**
 * imeplementation of {@link IOutboundHttpConfig} applying a proxy to the http client<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public class OutboundConfigImplProxy implements IOutboundHttpConfig, IOutboundFtpConfig {

    private final HttpHost httpProxy;
    private final Proxy ftpProxy;

    public OutboundConfigImplProxy(final String host, final int port) {
        this.httpProxy = new HttpHost(host, port);
        this.ftpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

    @Override
    public HttpClientBuilder applyTo(HttpClientBuilder builder) {
        ELogger.HTTP.debug(() -> String.format("applying proxy settings to http client builder (host: %s, port: %s)", this.httpProxy.getHostName(), this.httpProxy.getPort()));
        return builder.setProxy(this.httpProxy);
    }

    @Override
    public FTPClient applyTo(FTPClient config) {
        config.setProxy(this.ftpProxy);
        return config;
    }

}
