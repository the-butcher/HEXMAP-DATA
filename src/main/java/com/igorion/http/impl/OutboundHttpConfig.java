package com.igorion.http.impl;

import com.igorion.http.IOutboundHttpConfig;
import com.igorion.http.IOutboundHttpConfigEdit;

/**
 * accessor util to {@link IOutboundHttpConfig} instances
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public class OutboundHttpConfig {

    private OutboundHttpConfig() {
        //no public instance
    }

    /**
     * create a new instance of {@link IOutboundHttpConfigEdit} ready for accepting sub-configs;
     *
     * @return
     */
    public static IOutboundHttpConfigEdit composite() {
        return new OutboundHttpConfigCompositeImpl();
    }

    /**
     * create a new instance of {@link IOutboundHttpConfig} that applies request timeouts
     *
     * @param connectTimeout
     * @param socketTimeout
     * @return
     */
    public static IOutboundHttpConfig timeouts(final int connectTimeout, final int socketTimeout) {
        return new OutboundHttpConfigImplTimeouts(connectTimeout, socketTimeout);
    }

    /**
     * create a new instance of {@link IOutboundHttpConfig} that applies proxy settings
     *
     * @param host
     * @param port
     * @return
     */
    public static IOutboundHttpConfig proxy(final String host, final int port) {
        return new OutboundConfigImplProxy(host, port);
    }

    /**
     * create a new instance of {@link IOutboundHttpConfig} that will ignore all certificate errors
     *
     * @return
     */
    public static IOutboundHttpConfig noopSsl() {
        return new OutboundHttpConfigImplNoopSsl();
    }

}
