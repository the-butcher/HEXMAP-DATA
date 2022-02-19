package com.igorion.ftp.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.ftp.IFtpFileRoute;
import com.igorion.ftp.IFtpHandler;
import com.igorion.ftp.IFtpServer;
import com.igorion.type.json.DataResults;
import com.igorion.type.json.impl.JsonTypeImplDataResult;

public class FtpHandlerImpl implements IFtpHandler {

    private IFtpServer ftpServer;

    public FtpHandlerImpl(IFtpServer ftpServer) {
        this.ftpServer = ftpServer;
    }

    protected JsonTypeImplDataResult handleFileUpload(FTPClient ftpClient, IFtpFileRoute fileUpload) throws IOException {
        try (InputStream inputStream = new FileInputStream(fileUpload.getSource())) {
            ftpClient.allocate(inputStream.available());
            boolean success = ftpClient.storeFile(fileUpload.getTarget().getPath(), inputStream);
            return DataResults.create(fileUpload.getSource().getAbsolutePath(), this.ftpServer.getServerUrl() + "/" + fileUpload.getTarget().getPath(), success);
        }
    }

    @Override
    public List<JsonTypeImplDataResult> upload(List<IFtpFileRoute> fileRoutes) {

        FTPClient ftpClient = new FTPClient();
        try {

            System.out.println("-".repeat(100));

            List<JsonTypeImplDataResult> results = new ArrayList<>();

            System.out.println("ftp, connecting (" + this.ftpServer.getServerUrl() + "@" + this.ftpServer.getServerPort() + ") ...");

            ftpClient.connect(this.ftpServer.getServerUrl(), this.ftpServer.getServerPort());
            ftpClient.login(this.ftpServer.getUsername(), this.ftpServer.getPassword());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);

//            ftpClient.setControlKeepAliveTimeout(1000);
            ftpClient.enterLocalPassiveMode();

            for (IFtpFileRoute fileRoute : fileRoutes) {
                System.out.println("ftp, uploading (" + fileRoute.getSource().getName() + " >> " + fileRoute.getTarget().getPath() + ") ...");
                results.add(handleFileUpload(ftpClient, fileRoute));
            }

            return results;

        } catch (IOException ioex1) {
            throw new C19Failure(EFailureCode.FTP_FAILURE, "failed to handle ftp-server data-upload", ioex1);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    System.out.println("ftp, disconnecting ...");
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ioex2) {
                throw new C19Failure(EFailureCode.FTP_FAILURE, "failed to close ftp-connection", ioex2);
            }
            System.out.println("-".repeat(100));

        }

    }

}
