package com.igorion.http;

import org.apache.http.impl.client.CloseableHttpClient;

/**
 * extension to {@link IOutboundHttpConfig} that can multiply child instances of {@link IOutboundHttpConfig}<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public interface IOutboundHttpConfigEdit {

    /**
     * check if an instance of the given type is present
     * @param type
     * @return
     */
    boolean hasSubConfig(Class<? extends IOutboundHttpConfig> type);

    /**
     * add a sub instance of {@link IOutboundHttpConfig}
     *
     * @param config
     */
    void addSubConfig(IOutboundHttpConfig subConfig);

    /**
     * get an instance of {@link IOutboundHttpConfigEdit} that may apply settings like timeouts, ssl context, proxy, ...) to {@link CloseableHttpClient}s
     *
     * @return
     */
    IOutboundHttpConfig getOutboundHttpConfig();

}
