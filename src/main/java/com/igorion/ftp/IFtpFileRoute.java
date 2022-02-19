package com.igorion.ftp;

import java.io.File;

public interface IFtpFileRoute {

    File getSource();

    IFtpResource getTarget();

}
