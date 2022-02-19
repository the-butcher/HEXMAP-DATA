package com.igorion.http.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.igorion.http.IResponseHandler;
import com.igorion.http.IResponseHandlerJsonTyped;
import com.igorion.http.IResponseHandlerRawContent;
import com.igorion.type.json.IJsonTypeResponse;

/**
 * accessor util to {@link IResponseHandler} instances<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public class ResponseHandler {

    private ResponseHandler() {
        //no public instance
    }

    /**
     * create a new instance of {@link IResponseHandler} that will provide json output
     *
     * @return
     */
    public static IResponseHandlerRawContent forRawContent() {
        return new ResponseHandlerRawContentImpl();
    }

    /**
     * create a new instance of {@link IResponseHandler} that will provide json output
     *
     * @return
     */
    public static <T extends IJsonTypeResponse> IResponseHandlerJsonTyped<T> forJsonTyped(Class<T> outputEntityType) {
        return new ResponseHandlerJsonTypedImpl<>(outputEntityType);
    }

    /**
     * create a new instance of {@link IResponseHandler} parsing a generic {@link JsonNode} object
     * @return
     */
    public static <N extends JsonNode> IResponseHandler<N> forJsonNode(Class<N> outputEntityType) {
        return new ResponseHandlerJsonNodeImpl<>(outputEntityType);
    }

}
