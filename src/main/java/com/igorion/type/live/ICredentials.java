package com.igorion.type.live;

/**
 * definition for a type that describes credentials, regarding the referer as part of the credentials<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public interface ICredentials {

    /**
     * get the username associated with this instance
     *
     * @return
     */
    String getUsername();

    /**
     * get the password associated with this instance
     *
     * @return
     */
    IPassword getPassword();

}
