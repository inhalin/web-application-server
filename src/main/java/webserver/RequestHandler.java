package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.dispatch.Dispatcher;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.requestParser.HttpRequestParser;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream())
        {
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            HttpRequest request = HttpRequestParser.parse(br);
            log.info("method = {}, url : {}, content length : {}",
                    request.getMethod(), request.getUrl(), request.getHeader().getContentLength());

            HttpResponse response = Dispatcher.dispatch(request);

            byte[] body = response.getBody().toByte();
            response200Header(dos, response);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, HttpResponse response) {
        try {
            dos.writeBytes("HTTP/1.1 " + response.getStatusCode().getCode() + " " +  response.getStatusCode().getText() + "\r\n");
            dos.writeBytes("Content-Type: " + response.getHeader().getContentType() + "\r\n");
            dos.writeBytes("Content-Length: " + response.getHeader().getContentLength() + "\r\n");
            dos.writeBytes("Location: " + response.getHeader().getLocation() + "\r\n");
            dos.writeBytes("Set-Cookie: " + response.getHeader().getSetCookie() + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
