package com.igorion.ftp.impl;

import java.io.File;

import com.igorion.ftp.IFtpFileRoute;
import com.igorion.ftp.IFtpResource;

public class FtpFileRouteImpl implements IFtpFileRoute {

    private final File source;
    private final IFtpResource target;

    public FtpFileRouteImpl(File source, IFtpResource target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public File getSource() {
        return this.source;
    }

    @Override
    public IFtpResource getTarget() {
        return this.target;
    }

}
