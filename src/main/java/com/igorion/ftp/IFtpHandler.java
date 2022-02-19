package com.igorion.ftp;

import java.util.List;

import com.igorion.type.json.impl.JsonTypeImplDataResult;

public interface IFtpHandler {

    List<JsonTypeImplDataResult> upload(List<IFtpFileRoute> fileRoutes);

}
