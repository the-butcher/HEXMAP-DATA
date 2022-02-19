package com.igorion.type.json.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.igorion.type.json.IJsonType;
import com.igorion.type.json.IJsonTypeError;

/**
 * json mapping for an arcgis server error response<br>
 *
 * <pre>
 * {
 *   "error": {
 *     "code": 401,
 *     "message": "You are not authorized to access this information",
 *     "details": "Invalid credentials"
 *   }
 * }
 * </pre>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTypeImplError implements IJsonType, IJsonTypeError {

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getDetails() {
        return null;
    }

}
