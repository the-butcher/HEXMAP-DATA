package com.igorion.http;

import com.igorion.fail.impl.C19Failure;

/**
 * definition for a type that can make specific requests in order to know which {@link EAuthenticateScheme} is required by that endpoint
 *
 * @author h.fleischer
 * @since 13.03.2019
 *
 */
public interface IAuththenticationSchemeLoader {

    EAuthenticateScheme loadAuthenticationScheme() throws C19Failure;

}
