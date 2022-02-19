package com.igorion.app.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.igorion.app.IC19Application;
import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.ftp.IFtpServer;
import com.igorion.http.IOutboundFtpConfig;
import com.igorion.http.IOutboundHttpConfig;
import com.igorion.http.IOutboundHttpConfigEdit;
import com.igorion.http.impl.OutboundConfigImplProxy;
import com.igorion.http.impl.OutboundHttpConfig;
import com.igorion.logs.ELogger;

public class C19ApplicationImpl implements IC19Application {

    private IOutboundHttpConfigEdit outboundHttpConfigEdit;

    private List<IFtpServer> ftpServers;

    private OutboundConfigImplProxy proxyConfig;

    public C19ApplicationImpl(String rootPathAbsolute) {
        this.outboundHttpConfigEdit = OutboundHttpConfig.composite();
        this.ftpServers = new ArrayList<>();
    }

    @Override
    public List<IFtpServer> getFtpServers() {
        return this.ftpServers;
    }

    @Override
    public synchronized void loadConfiguration() {

        try {

            ELogger.APPLICATION.info(() -> String.format("loading application"));

            //reset http config -- do this first, so the handler setup already used the correct values
            this.outboundHttpConfigEdit = OutboundHttpConfig.composite();

        } catch (Exception ex) {
            ELogger.APPLICATION.warn(new C19Failure(EFailureCode.MISCONFIGURED, "failed to load application", ex));
        }

    }

    @Override
    public boolean hasSubConfig(Class<? extends IOutboundHttpConfig> type) {
        return this.outboundHttpConfigEdit.hasSubConfig(type);
    }

    @Override
    public void addSubConfig(IOutboundHttpConfig subConfig) {
        this.outboundHttpConfigEdit.addSubConfig(subConfig);
    }

    @Override
    public IOutboundHttpConfig getOutboundHttpConfig() {
        return this.outboundHttpConfigEdit.getOutboundHttpConfig();
    }

    @Override
    public Optional<IOutboundFtpConfig> optOutboundFtpConfig() {
        return Optional.of(this.proxyConfig);
    }

}
