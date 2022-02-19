package com.igorion.type.live.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.igorion.type.live.ICredentials;

public class Credentials {

    private Credentials() {
        //no public instance
    }

    public static Optional<ICredentials> optCredentials(String username, String password) {
        if (StringUtils.isNoneEmpty(username, password)) {
            return Optional.of(new CredentialsImpl(username, Passwords.create(password)));
        } else {
            return Optional.empty();
        }
    }

}
