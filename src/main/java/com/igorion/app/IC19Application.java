package com.igorion.app;

import java.util.List;
import java.util.Optional;

import com.igorion.ftp.IFtpServer;
import com.igorion.http.IOutboundFtpConfig;
import com.igorion.http.IOutboundHttpConfigEdit;

public interface IC19Application extends IOutboundHttpConfigEdit {

    Optional<IOutboundFtpConfig> optOutboundFtpConfig();

    /**
     * load or reload the configuration file<br>
     * clean reloadability is important so the app (or the app-container) does not have to be restarted<br>
     * therefore it is also important that no instances hold references to {@link IReportHandler}s or similar<br>
     */
    void loadConfiguration();

    /**
     * get ftp-server, to which the data shall be uploaded
     * @return
     */
    List<IFtpServer> getFtpServers();

}
