package io;

import http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpMessageReader implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(HttpMessageReader.class);
    private BufferedReader reader;
    private HttpMethod method;
    private String url;
    private String httpVersion;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String body;

    public HttpMessageReader(InputStream in) {
        reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        headers = new HashMap<>();
    }

    public void printHeader() throws IOException {
        String line = "start";
        while (!"".equals(line)) {
            line = reader.readLine();
            if (line == null) return;
            log.info(line);
        }
    }

    public String readUrlPath() throws IOException {
        String line = reader.readLine();
        if (line == null) throw new IOException();
        String[] splited = line.split(" ");
        method = HttpMethod.valueToMethod(splited[0]);
        url = splited[1];
        httpVersion = splited[2];

        splited = url.split("\\?");
        if (splited.length == 1) return url;
        url = splited[0];
        queryParams = HttpRequestUtils.parseQueryString(splited[1]);
        return url;
    }

    private String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> readBodyMap() throws IOException {
        parseHeaders();
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        body = IOUtils.readData(reader, contentLength);
        return HttpRequestUtils.parseQueryString(body);
    }

    public void parseHeaders() throws IOException {
        String line = "start";
        while (!"".equals(line)) {
            line = reader.readLine();
            if (line == null || line.equals(""))  return;

            int index = line.indexOf(":");
            String key = line.substring(0, index);
            String value = line.substring(index + 1);
            headers.put(key, value.strip());
        }
    }

    public Map<String, String> getHeaders() throws IOException {
        parseHeaders();
        return headers;
    }
}
