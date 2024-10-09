package red.lixiang.dd;


import red.lixiang.dd.tools.ToolsLogger;
import com.github.kwhat.jnativehook.DefaultLibraryLocator;
import com.github.kwhat.jnativehook.NativeLibraryLocator;
import com.github.kwhat.jnativehook.NativeSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class JLibLocator implements NativeLibraryLocator {


    DefaultLibraryLocator defaultLocator = new DefaultLibraryLocator();

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
        // 和 build.gradle中 jink输出的目录是一致的
        var lib = jhome + File.separator +"bin"+File.separator+"native-libs"+ File.separator + os + File.separator + arch + File.separator + libName;
        var libFile = new File(lib);
        ToolsLogger.info("lib path: " + libFile.getAbsolutePath());
        if(!libFile.exists()){
            return defaultLocator.getLibraries();
        }
        libs.add(libFile);

        return libs.iterator();
    }
}
