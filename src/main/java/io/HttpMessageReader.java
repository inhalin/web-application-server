package io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HttpMessageReader implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(HttpMessageReader.class);
    private BufferedReader reader;
    private String method;
    private String url;
    private String httpVersion;

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
        method = splited[0];
        url = splited[1];
        httpVersion = splited[2];
        return url;
    }

    private String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
