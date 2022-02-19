package com.igorion.ftp;

import java.util.Optional;
import java.util.function.Function;

import com.igorion.type.live.ILiveType;

public interface IFtpServer extends ILiveType {

    default Optional<String> optResourceUrl(Function<IFtpServer, Optional<IFtpResource>> supplierOfFtpResource) {
        return supplierOfFtpResource.apply(this).map(ftpResource -> String.format("%s/%s", getServerUrl(), ftpResource.getPath()));
    }

    /**
     * get the url of the ftp server (must not include a trailing slash), ie:<br>
     * <li>ftp://ftp2215154@ftp70.world4you.com
     *
     * @return
     */
    String getServerUrl();

    int getServerPort();

    String getUsername();

    String getPassword();

}
