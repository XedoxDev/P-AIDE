package com.xedox.paide.utils.compiler;

import dalvik.system.DexClassLoader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DexLoader {

    public static <T> T loadClass(
            String className,
            String dexPath,
            String librarySearchPath,
            android.content.Context context)
            throws ClassNotFoundException,
                    InstantiationException,
                    IllegalAccessException,
                    InvocationTargetException,
                    NoSuchMethodException {

        String optimizedDirectory = context.getCacheDir().getAbsolutePath();

        DexClassLoader classLoader =
                new DexClassLoader(
                        dexPath,
                        optimizedDirectory,
                        librarySearchPath,
                        ClassLoader.getSystemClassLoader());

        Class<?> clazz = classLoader.loadClass(className);
        Constructor<?> constructor = clazz.getConstructor();
        return (T) constructor.newInstance();
    }
}
