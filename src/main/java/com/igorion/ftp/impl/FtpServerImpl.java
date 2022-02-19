package com.igorion.ftp.impl;

import com.igorion.ftp.IFtpServer;

public class FtpServerImpl implements IFtpServer {

    private final String serverUrl;
    private final int serverPort;
    private final String username;
    private final String password;

    public FtpServerImpl(String serverUrl, int serverPort, String username, String password) {
        this.serverUrl = serverUrl;
        this.serverPort = serverPort;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getServerUrl() {
        return this.serverUrl;
    }

    @Override
    public int getServerPort() {
        return this.serverPort;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String toString() {
        return String.format("%s [server-url: %s, username: %s]", getClass().getSimpleName(), this.serverUrl, this.username);
    }

}
