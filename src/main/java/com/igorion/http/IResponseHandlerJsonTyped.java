package com.igorion.http;

import com.igorion.type.json.IJsonType;

/**
 * definition of a type that handles HTTP responses and provides a {@link IJsonType} result
 *
 * @author h.fleischer
 * @since @since 16.06..2019
 *
 */
public interface IResponseHandlerJsonTyped<T extends IJsonType> extends IResponseHandler<T> {

    /**
     * get the expected output type
     *
     * @return
     */
    Class<T> getOutputEntityType();

}
