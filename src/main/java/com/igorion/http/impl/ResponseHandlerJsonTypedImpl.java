package com.igorion.http.impl;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.fail.impl.OutboundAuthenticationFailure;
import com.igorion.http.EAuthenticateScheme;
import com.igorion.http.IHttpResult;
import com.igorion.http.IResponseHandlerJsonTyped;
import com.igorion.type.json.IJsonTypeError;
import com.igorion.type.json.IJsonTypeResponse;

class ResponseHandlerJsonTypedImpl<T extends IJsonTypeResponse> implements IResponseHandlerJsonTyped<T> {

    private final Class<T> outputEntityType;

    ResponseHandlerJsonTypedImpl(Class<T> outputEntityType) {
        this.outputEntityType = outputEntityType;
    }

    @Override
    public Class<T> getOutputEntityType() {
        return this.outputEntityType;
    }

    @Override
    public IHttpResult<T> handleResponse(HttpResponse httpResponse) {
        return handleUnhandledAuthentication(httpResponse).orElseGet(() -> handleResponseEntity(httpResponse));
    }

    public IHttpResult<T> handleResponseEntity(HttpResponse httpResponse) {

        try {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            T jsonOutput = new ObjectMapper().readValue(httpResponse.getEntity().getContent(), getOutputEntityType());
            IJsonTypeError jsonError = jsonOutput.getError();
            if (jsonError != null) {
                return new HttpResultImpl<>(jsonError.getCode(), new C19Failure(EFailureCode.INVALID_GATEWAY, jsonError.getMessage(), null));
            } else {
                return new HttpResultImpl<>(statusCode, jsonOutput);
            }
        } catch (IOException ioFailure) {
            return new HttpResultImpl<>(500, new C19Failure(EFailureCode.INVALID_GATEWAY, "failed to parse response", ioFailure));
        }

    }

    public Optional<IHttpResult<T>> handleUnhandledAuthentication(HttpResponse httpResponse) {

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
