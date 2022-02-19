package com.igorion.logs;

import java.util.function.Supplier;

import com.igorion.fail.impl.C19Failure;

/**
 * definition for types that provide "lazy" logging, like formatting a string only just before it actually get´s logged
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public interface ILazyLogger {

    void debug(Supplier<String> supplierOfMessage);

    void info(Supplier<String> supplierOfMessage);

    @Deprecated
    void warn(Supplier<String> supplierOfMessage, Exception ex);

    void warn(C19Failure failure);

}
