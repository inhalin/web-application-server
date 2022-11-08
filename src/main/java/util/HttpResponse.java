package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private final Map<String, String> header = new HashMap<>();
    private DataOutputStream dos = null;

    public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void forward(String url) {
        try {
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

            putContentTypeToHeader(url);
            putContentLengthToHeader(body);
            response200Header(body.length);
            responseBody(body);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    public void forwardBody(byte[] body) {
        try {
            putContentLengthToHeader(body);
            response200Header(body.length);
            responseBody(body);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void putContentTypeToHeader(String url) {
        if (url.endsWith(".html")) {
            header.put("Content-Type", "text/html;charset=utf-8");
        } else if (url.endsWith(".css")) {
            header.put("Content-Type", "text/css");
        } else if (url.endsWith(".js")) {
            header.put("Content-Type", "application/javascript");
        }
    }

    private void putContentLengthToHeader(byte[] body) {
        header.put("Content-Length", String.valueOf(body.length));
    }

    private void response200Header(int bodyLength) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK\r\n");
        setHeader();
        dos.writeBytes("\r\n");
    }

    private void setHeader() {
        Set<String> keys = header.keySet();

        try {
            for (String key : keys) {
                dos.writeBytes(key + ": " + header.get(key) + "\r\n");
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    public void sendRedirect(String redirectTo) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            addHeader("Location", redirectTo);
            setHeader();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }
}
