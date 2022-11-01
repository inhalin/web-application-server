package io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import webserver.RequestHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpMessageReader implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(HttpMessageReader.class);
    private BufferedReader reader;
    private HttpMethod method;
    private String url;
    private String httpVersion;
    private Map<String, String> queryParams;

    public HttpMessageReader(InputStream in) {
        reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public void print() throws IOException {
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
}
