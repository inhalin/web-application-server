package util;

import java.util.HashMap;
import java.util.Map;

public class CustomUtils {
    public static Map<String, String> getRequestPath(String line) {
        if (line == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        String[] tokens = line.split(" ");

        map.put("method", tokens[0]);

        if (tokens[1].contains("?")) {
            String[] url = tokens[1].split("[?]");

            map.put("path", url[0]);
            map.put("queryString", url[1]);
        } else {
            map.put("path", tokens[1]);
            map.put("queryString", null);
        }

        return map;
    }

    public static Map<String, String> getHeaderInfo(String line) {
        if (line == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        String[] tokens = line.split(" ");

        map.put(tokens[0], tokens[1]);

        return map;
    }
}
