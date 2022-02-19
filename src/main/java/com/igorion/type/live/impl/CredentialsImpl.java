package com.igorion.type.live.impl;

import org.apache.commons.lang3.StringUtils;

import com.igorion.type.live.ICredentials;
import com.igorion.type.live.IPassword;

/**
 * simple implementation if {@link ICredentials}<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
class CredentialsImpl implements ICredentials {

    private final String username;
    private final IPassword password;

    CredentialsImpl(String username, IPassword password) {
        this.username = StringUtils.isNotBlank(username) ? username.trim() : "";
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public IPassword getPassword() {
        return this.password;
    }

}
