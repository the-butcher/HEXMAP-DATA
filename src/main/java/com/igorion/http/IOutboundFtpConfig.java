package com.igorion.http;

import org.apache.commons.net.ftp.FTPClient;

/**
 * definition for types that can apply a setting to a {@link FTPClient} instances<br>
 * this is usually done right before issuing a http request<br>
 *
 * @author h.fleischer
 * @since 20.08.2020
 *
 */
@FunctionalInterface
public interface IOutboundFtpConfig {

    /**
     * apply something to the given {@link FTPClient} instance, then return the altered instance
     *
     * @param builder
     * @return
     */
    FTPClient applyTo(FTPClient ftpClient);

}
