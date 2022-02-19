package com.igorion.fail.impl;

import com.igorion.fail.EFailureCode;
import com.igorion.http.EAuthenticateScheme;

/**
 * sptecific type of {@link OutboundRequestFailure} for the case when an unhandled authentication occurs<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public class OutboundAuthenticationFailure extends C19Failure {

    private static final long serialVersionUID = -1339589831416013058L;

    private final EAuthenticateScheme authenticationScheme;

    public OutboundAuthenticationFailure(String message, EAuthenticateScheme authenticationScheme, Throwable cause) {
        super(EFailureCode.AUTHENTICATION_FAILURE, message, cause);
        this.authenticationScheme = authenticationScheme;
    }

    public EAuthenticateScheme getAuthenticationScheme() {
        return this.authenticationScheme;
    }

    @Override
    public String toString() {
        return String.format("%s [scheme: %s]", getClass().getSimpleName(), this.authenticationScheme.getSchemeName());
    }

}
