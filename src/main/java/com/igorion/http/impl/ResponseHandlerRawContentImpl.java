package com.igorion.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.fail.impl.OutboundAuthenticationFailure;
import com.igorion.http.EAuthenticateScheme;
import com.igorion.http.IHttpResult;
import com.igorion.http.IResponseHandlerRawContent;

class ResponseHandlerRawContentImpl implements IResponseHandlerRawContent {

    @Override
    public IHttpResult<byte[]> handleResponse(HttpResponse httpResponse) {
        return handleUnhandledAuthentication(httpResponse).orElseGet(() -> handleResponseEntity(httpResponse));
    }

    public static IHttpResult<byte[]> handleResponseEntity(HttpResponse httpResponse) {

        try (InputStream entityInput = httpResponse.getEntity().getContent()) {

            int statusCode = httpResponse.getStatusLine().getStatusCode();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = entityInput.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();

            return new HttpResultImpl<>(statusCode, buffer.toByteArray());

        } catch (IOException ioFailure) {
            return new HttpResultImpl<>(500, new C19Failure(EFailureCode.INVALID_GATEWAY, "failed to parse response", ioFailure));
        }

    }

    public static Optional<IHttpResult<byte[]>> handleUnhandledAuthentication(HttpResponse httpResponse) {

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
