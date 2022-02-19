package com.igorion.util.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorion.ftp.IFtpFileRoute;
import com.igorion.ftp.IFtpResource;
import com.igorion.ftp.IFtpServer;
import com.igorion.ftp.impl.FtpFileRouteImpl;
import com.igorion.ftp.impl.FtpHandlerImpl;
import com.igorion.ftp.impl.FtpResourceImpl;
import com.igorion.ftp.impl.FtpServerImpl;
import com.igorion.type.json.impl.JsonTypeImplHexmapDataRoot;

public class Storage {

    public static final File FOLDER___WORK = new File("C:\\privat\\_projects_cov\\covid2019_hexmap_data");
    public static final File FOLDER_TARGET = new File("C:\\privat\\_projects_cov\\covid2019_hexagon01\\public");

    protected static IFtpServer ftpServerInstance;

    private Storage() {
        // no public instance
    }

    protected static Optional<IFtpServer> optFtpServerInstance() throws Exception {

        if (ftpServerInstance == null) {

            File ftpPropertiesFile = new File("ftp.properties");
            if (ftpPropertiesFile.exists()) {
                try (InputStream ftpPropertiesInput = new FileInputStream(ftpPropertiesFile)) {

                    Properties ftpProperties = new Properties();
                    ftpProperties.load(ftpPropertiesInput);

                    String host = ftpProperties.getProperty("host");
                    int port = getNumericOrElseDefault(ftpProperties.getProperty("port"), 21);

                    String user = ftpProperties.getProperty("user");
                    String pass = ftpProperties.getProperty("pass");
                    ftpServerInstance = new FtpServerImpl(host, port, user, pass);

                }

            } else {
                System.err.println("please create file (" + ftpPropertiesFile.getAbsolutePath() + ") having [host,port,user,pass] properties for auto ftp upload");
            }

        }
        return Optional.ofNullable(ftpServerInstance);

    }

    public static int getNumericOrElseDefault(String strNum, int defaultValue) {
        if (strNum == null) {
            return defaultValue;
        }
        int numericValue = defaultValue;
        try {
            numericValue = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            System.err.println("failed to parse string value (" + strNum + ") to number, using default value (" + defaultValue + ")");
        }
        return numericValue;
    }

    public static void store(JsonTypeImplHexmapDataRoot... dataRoots) throws Exception {

        for (JsonTypeImplHexmapDataRoot dataRoot : dataRoots) {
            File hexmapFile = new File(Storage.FOLDER_TARGET, dataRoot.getFileName());
            new ObjectMapper().writeValue(hexmapFile, dataRoot); // .writerWithDefaultPrettyPrinter()
        }

        Optional<IFtpServer> oFtpServer = optFtpServerInstance();
        if (oFtpServer.isPresent()) {

            IFtpServer ftpServer = oFtpServer.get();

            List<IFtpFileRoute> fileRoutes = new ArrayList<>();
            for (JsonTypeImplHexmapDataRoot dataRoot : dataRoots) {
                File hexmapFile = new File(Storage.FOLDER_TARGET, dataRoot.getFileName());
                IFtpResource ftpResource = new FtpResourceImpl(String.format("hexmap/%s", dataRoot.getFileName()));
                fileRoutes.add(new FtpFileRouteImpl(hexmapFile, ftpResource));
            }

            new FtpHandlerImpl(ftpServer).upload(fileRoutes);

        }

    }

}
