package com.example.ttplayer;


import com.github.kwhat.jnativehook.NativeLibraryLocator;
import com.github.kwhat.jnativehook.NativeSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class JLibLocator implements NativeLibraryLocator {
    /**
     * This method is used to regsiter the Locator.
     */
    public static void setAaDefaultLocator() {
        System.setProperty("jnativehook.lib.locator", JLibLocator.class.getCanonicalName());
    }

    /**
     * Locates the native libraries.
     */
    @Override
    public Iterator<File> getLibraries() {
        var libs = new ArrayList<File>(1);
        var os = NativeSystem.getFamily().toString().toLowerCase();
        var arch = NativeSystem.getArchitecture().toString().toLowerCase();
        var jhome = System.getProperty("java.home");
        var libName = System.mapLibraryName("JNativeHook");
        var lib = jhome + File.separator + os + File.separator + arch + File.separator + libName;
        var libFile = new File(lib);

        libs.add(libFile);

        return libs.iterator();
    }
}
