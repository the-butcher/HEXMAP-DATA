package com.igorion.type.live.impl;

import org.apache.commons.lang3.StringUtils;

import com.igorion.type.live.IPassword;

class PasswordImpl implements IPassword {

    private final String value;

    PasswordImpl(String value) {
        this.value = StringUtils.isNotBlank(value) ? value.trim() : "";
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.format("%s [value: ******]", getClass().getSimpleName());
    }

}
