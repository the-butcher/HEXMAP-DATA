package com.igorion.type.live.impl;

import com.igorion.type.live.IPassword;

/**
 * accessor util to {@link IPassword} instances<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public class Passwords {

    private Passwords() {
        //no public intance
    }

    public static IPassword create(String value) {
        return new PasswordImpl(value);
    }

}
