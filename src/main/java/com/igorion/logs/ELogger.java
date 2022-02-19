package com.igorion.logs;

import java.util.function.Supplier;

import org.apache.log4j.Logger;

import com.igorion.fail.impl.C19Failure;

public enum ELogger implements ILazyLogger {

    APPLICATION("com.vertigis.application"),
    //PROXY("com.vertigis.proxy"),
    PARSE("com.vertigis.parse"),
    HTTP("com.vertigis.http");

    private final Logger delegate;

    private ELogger(String name) {
        this.delegate = Logger.getLogger(name);
    }

    public Logger getLogger() {
        return this.delegate;
    }

    @Override
    public void debug(Supplier<String> supplierOfMessage) {
        if (this.delegate.isDebugEnabled()) {
            this.delegate.debug(supplierOfMessage.get());
        }
    }

    @Override
    public void info(Supplier<String> supplierOfMessage) {
        if (this.delegate.isInfoEnabled()) {
            this.delegate.info(supplierOfMessage.get());
        }
    }

    @Override
    public void warn(C19Failure failure) {

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(String.format("%s%n", failure.getCode()));

        Throwable cause = failure;
        StackTraceElement[] stackTrace;
        while (cause != null) {
            stackTrace = cause.getStackTrace();
            if (stackTrace.length > 0) {
                messageBuilder.append(String.format("caused by: %-100s at %s.%s#%s%n", cause.getMessage(), stackTrace[0].getClassName(), stackTrace[0].getMethodName(), stackTrace[0].getLineNumber()));
            } else {
                messageBuilder.append(String.format("caused by: %s%n", cause.getMessage()));
            }
            cause = cause.getCause();
        }
        this.delegate.warn(messageBuilder.toString());

    }

    @Deprecated
    @Override
    public void warn(Supplier<String> supplierOfMessage, Exception ex) {

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(String.format("%s%n", supplierOfMessage.get()));

        Throwable cause = ex;
        StackTraceElement[] stackTrace;
        while (cause != null) {
            stackTrace = cause.getStackTrace();
            if (stackTrace.length > 0) {
                messageBuilder.append(String.format("caused by: %-100s at %s.%s#%s%n", cause.getMessage(), stackTrace[0].getClassName(), stackTrace[0].getMethodName(), stackTrace[0].getLineNumber()));
            } else {
                messageBuilder.append(String.format("caused by: %s%n", cause.getMessage()));
            }
            cause = cause.getCause();
        }
        this.delegate.warn(messageBuilder.toString());

    }

}
