package com.igorion.type.live;

/**
 * definition of a type that holds a password<br>
 * a specific type has been created to enable i.e. masking in logging<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public interface IPassword {

    /**
     * get the unmasked value of this password
     *
     * @return
     */
    String getValue();

}
