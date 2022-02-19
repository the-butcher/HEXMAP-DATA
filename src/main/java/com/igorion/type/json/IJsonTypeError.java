package com.igorion.type.json;

/**
 * definition for a type that contains an error sent with a json response<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public interface IJsonTypeError {

    int getCode();

    String getMessage();

    String getDetails();

}
