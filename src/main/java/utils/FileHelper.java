package utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class FileHelper {
    public static URL getTruePathURL(String s) {
        String truePath = getTruePath(s);
        try {
            return new File(truePath).toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTruePath(String s) {
        return new File(s).getAbsolutePath().replace("remember-me", "RememberMeJavaFX");
    }
}
