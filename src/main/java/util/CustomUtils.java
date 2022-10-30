package util;

public class CustomUtils {
    public static String getRequestPath(String header) {
        if (header == null) {
            return null;
        }

        return header.split(" ")[1];
    }
}
