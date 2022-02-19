package com.igorion.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.igorion.type.json.IJsonType;

/**
 * definition of a type that handles HTTP responses and provides a {@link IJsonType} result
 *
 * @author h.fleischer
 * @since 08.03.2019
 *
 */
public interface IResponseHandlerJsonNode<N extends JsonNode> extends IResponseHandler<N> {

    /**
     * get the expected output type
     *
     * @return
     */
    Class<N> getOutputEntityType();

}
