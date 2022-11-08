package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CustomUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private StatusCode statusCode;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;
    private DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String url) throws IOException {
        statusCode = StatusCode.OK;

        try {
            body = Files.readAllBytes(new File(CustomUtils.WEB_APP_ROOT + url).toPath());

            addHeader("Content-Type", ContentType.findByExt(CustomUtils.getFileExt(url)));
            addHeader("Content-Length", String.valueOf(body.length));

            response200Header();
            responseBody(body);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
    }

    public void forwardBody(String bodyStr) {
        statusCode = StatusCode.OK;
        body = bodyStr.getBytes();

        addHeader("Content-Type", "text/html");
        addHeader("Content-Length", String.valueOf(body.length));

        response200Header();
        responseBody(body);
    }

    public void sendRedirect(String url) {
        statusCode = StatusCode.REDIRECT;

        try {
            body = Files.readAllBytes(new File(CustomUtils.WEB_APP_ROOT + url).toPath());

            addHeader("Content-Type", ContentType.findByExt(CustomUtils.getFileExt(url)));
            addHeader("Content-Length", String.valueOf(body.length));
            addHeader("Location", url);

            response302Header();
            responseBody(body);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    private void responseBody(byte[] body) {
        try {
            this.dos.write(body, 0, body.length);
            this.dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header() {
        try {
            dos.writeBytes("HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getMessage() + " \r\n");
            dos.writeBytes("Content-Type: " + headers.get("Content-Type") + ";charset=utf-8 \r\n");
            dos.writeBytes("Content-Length: " + headers.get("Content-Length") + " \r\n");

            dos.writeBytes("\r\n");

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header() {
        try {
            dos.writeBytes("HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getMessage() + " \r\n");
            dos.writeBytes("Content-Type: " + headers.get("Content-Type") + ";charset=utf-8 \r\n");
            dos.writeBytes("Content-Length: " + headers.get("Content-Length") + " \r\n");
            dos.writeBytes("Location: " + headers.get("Location") + " \r\n");

            if (headers.containsKey("Set-Cookie")) {
                dos.writeBytes("Set-Cookie: " + headers.get("Set-Cookie") + " \r\n");
            }
            dos.writeBytes("\r\n");

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
