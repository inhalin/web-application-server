package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private String method;
    private String path;
    private Map<String, String> parameter = new HashMap<>();
    private Map<String, String> header = new HashMap<>();

    public HttpRequest(InputStream in) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();

            if (line == null) return;

            method = line.split(" ")[0];
            String[] tokens = line.split(" ")[1].split("\\?");
            path = tokens[0];

            if (tokens.length > 1) {
                setParameter(tokens[1]);
            }

            while (!"".equals(line = br.readLine())) {
                if (line == null) return;
                setHeader(line);
            }

            if (header.get("Content-Length") != null) {
                int contentLength = Integer.parseInt(header.get("Content-Length"));
                if (contentLength > 0) {
                    String token = IOUtils.readData(br, contentLength);
                    setParameter(token);
                }
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    public String getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public String getHeader(String field) {
        return header.get(field);
    }

    public String getParameter(String key) {
        return parameter.get(key);
    }

    private void setParameter(String token) {
            parameter = HttpRequestUtils.parseQueryString(token);
    }

    private void setHeader(String line) {
        String[] split = line.split(": ");
        header.put(split[0], split[1]);
    }
}
