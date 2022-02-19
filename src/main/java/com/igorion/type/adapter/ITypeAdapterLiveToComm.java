package com.igorion.type.adapter;

import com.igorion.fail.impl.C19Failure;
import com.igorion.type.comm.ICommType;
import com.igorion.type.live.ILiveType;

/**
 * definition for types that can supply a specific type of {@link ICommType}<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 * @param <L> the {@link ILiveType} managed by this instance
 * @param <C> the {@link ICommType} managed by this instance
 */
public interface ITypeAdapterLiveToComm<L extends ILiveType, C extends ICommType> {

    /**
     * converts a live-type to an appropriate comm-type
     *
     * @return
     */
    C adapt(L liveInstance) throws C19Failure;

}
