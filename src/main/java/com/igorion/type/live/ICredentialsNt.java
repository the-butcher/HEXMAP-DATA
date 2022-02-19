package com.igorion.type.live;

/**
 * extension to {@link ICredentials} storing the domain in a separate field
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public interface ICredentialsNt extends ICredentials {

    /**
     * get the users domain associated with this instance
     *
     * @return
     */
    String getDomain();

}
