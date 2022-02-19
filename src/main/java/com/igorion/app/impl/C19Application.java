package com.igorion.app.impl;

import java.util.Optional;

import com.igorion.app.IC19Application;
import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;

/**
 * accessor util to the singleton {@link IC19Application} instance<br>
 *
 * @author h.fleischer
 * @since 19.04.2020
 *
 */
public class C19Application {

    private static IC19Application instance = null;

    private C19Application() {
        //no public instance
    }

    public static void init(String rootPath) {
        instance = new C19ApplicationImpl(rootPath);
        instance.loadConfiguration();
    }

    public static synchronized void clear() {
        instance = null;
    }

    public static synchronized IC19Application getInstance() {
        return Optional.ofNullable(instance).orElseThrow(() -> new C19Failure(EFailureCode.INTERNAL_ERROR, "failed to find singleton application instance", null));
    }

}
