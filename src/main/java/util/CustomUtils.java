package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomUtils {
    private static final Logger log = LoggerFactory.getLogger(CustomUtils.class);
    public static final String WEB_APP_ROOT = "./webapp";

    public static String getFileExt(String url) {
        String[] tokens = url.split("[.]");
        String ext = tokens[tokens.length-1];

        return ext;
    }
}
