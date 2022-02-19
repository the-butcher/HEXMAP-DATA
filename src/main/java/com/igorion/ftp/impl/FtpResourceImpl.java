package com.igorion.ftp.impl;

import com.igorion.ftp.IFtpResource;

public class FtpResourceImpl implements IFtpResource {

    private final String path;

    public FtpResourceImpl(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String toString() {
        return String.format("%s [path: %s]", getClass().getSimpleName(), this.path);
    }

}
