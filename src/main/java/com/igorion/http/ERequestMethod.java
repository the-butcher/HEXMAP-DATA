package com.igorion.http;

/**
 * enumeration of known and handled http request methods<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public enum ERequestMethod {

    GET("GET"),
    POST("POST");

    private final String methodName;

    private ERequestMethod(final String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

}
