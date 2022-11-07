package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomUtils {
    private static final Logger log = LoggerFactory.getLogger(CustomUtils.class);

    public static Map<String, String> getStartLine(String line) {
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

    public static String getHeaderValue(BufferedReader br, String header) {
        String value = null;

        try {
            while (true) {
                String line = br.readLine();
                if (line == null || "".equals(line)) {
                    return null;
                }
                String[] tokens = line.split(" ");

                if (tokens[0].equals(header + ":")) {
                    value = tokens[1];
                    break;
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return value;
    }

    public static boolean isStaticResource(String requestPath) {
        String[] tokens = requestPath.split("[.]");
        String ext = tokens[tokens.length-1];

        return "js".equals(ext) || "css".equals(ext);
    }

    public static void moveEndLine(BufferedReader br) {
        try {
            while (!"".equals(br.readLine()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
