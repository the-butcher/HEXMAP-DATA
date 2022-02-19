package com.igorion.http.impl;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.fail.impl.OutboundAuthenticationFailure;
import com.igorion.http.EAuthenticateScheme;
import com.igorion.http.IHttpResult;
import com.igorion.http.IResponseHandlerJsonNode;

class ResponseHandlerJsonNodeImpl<N extends JsonNode> implements IResponseHandlerJsonNode<N> {

    private final Class<N> outputEntityType;

    ResponseHandlerJsonNodeImpl(Class<N> outputEntityType) {
        this.outputEntityType = outputEntityType;
    }

    @Override
    public Class<N> getOutputEntityType() {
        return this.outputEntityType;
    }

    @Override
    public IHttpResult<N> handleResponse(HttpResponse httpResponse) {
        return handleUnhandledAuthentication(httpResponse).orElseGet(() -> handleResponseEntity(httpResponse));
    }

    @SuppressWarnings("unchecked")
    public IHttpResult<N> handleResponseEntity(HttpResponse httpResponse) {
        try {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            JsonNode tree = new ObjectMapper().readTree(httpResponse.getEntity().getContent());
            if (getOutputEntityType().isAssignableFrom(tree.getClass())) {
                return new HttpResultImpl<>(statusCode, (N) tree);
            } else {
                throw new IOException("unexpected response type (expected: " + getOutputEntityType().getSimpleName() + ", invalid: " + tree.getClass().getSimpleName() + ")");
            }
        } catch (IOException ioFailure) {
            return new HttpResultImpl<>(500, new C19Failure(EFailureCode.INVALID_RESPONSE, "failed to parse response", ioFailure));
        }
    }

    public Optional<IHttpResult<N>> handleUnhandledAuthentication(HttpResponse httpResponse) {
        Header authenticateHeader = httpResponse.getFirstHeader("WWW-Authenticate");
        if (authenticateHeader != null && !StringUtils.isBlank(authenticateHeader.getValue())) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            EAuthenticateScheme unhandledScheme = EAuthenticateScheme.fromAuthenticationHeader(authenticateHeader.getValue());
            String message = String.format("failed to handle response due to unhandled authentication (status: %s, auth-scheme: %s)", statusCode, unhandledScheme);
            return Optional.of(new HttpResultImpl<>(statusCode, new OutboundAuthenticationFailure(message, unhandledScheme, null)));
        }
        return Optional.empty();
    }

}
