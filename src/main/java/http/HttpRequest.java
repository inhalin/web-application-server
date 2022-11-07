package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private BufferedReader br;
    private HttpMethod httpMethod;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();

    public HttpRequest(BufferedReader br) {
        try {
            this.br = br;

            parsingStartLine(br.readLine());
            parsingHeaders(br);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
    }

    public void setMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setParameter(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public HttpMethod getMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public void parsingStartLine(String startLine) {
        if (startLine == null) {
            throw new NullPointerException();
        }
        String[] tokens = startLine.split(" ");

        setMethod(HttpMethod.valueOf(tokens[0]));

        if (this.isExistQueryString(tokens[1])) {
            String[] urlInfo = tokens[1].split("[?]");
            setPath(urlInfo[0]);
            setParameter(HttpRequestUtils.parseQueryString(urlInfo[1]));
        } else {
            setPath(tokens[1]);
        }
    }
    public boolean isExistQueryString(String url) {
        return url.contains("?");
    }
    public void parsingHeaders(BufferedReader br) {
        Map<String, String> headers = new HashMap<>();

        try {
            String line = br.readLine();

            while (!"".equals(line) && line != null) {
                String[] tokens = line.split(" ");

                headers.put(tokens[0].replace(":", ""), tokens[1].trim());

                line = br.readLine();
            }
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }

        setHeaders(headers);
    }
}
