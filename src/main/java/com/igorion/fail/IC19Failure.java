package com.igorion.fail;

import com.igorion.fail.impl.C19Failure;
import com.igorion.type.adapter.ITypeAdapterLiveToComm;
import com.igorion.type.live.ILiveType;

/**
 * base type of {@link C19Failure} that helps fulfilling the contract for {@link ITypeAdapterLiveToComm}
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public interface IC19Failure extends ILiveType {

    String getMessage(int maxDetailCount);

    EFailureCode getCode();

    String[] getDetails(int maxDetailCount);

}
