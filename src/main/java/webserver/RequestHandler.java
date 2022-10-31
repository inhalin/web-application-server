package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import io.HttpMessageReader;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (HttpMessageReader requestReader = new HttpMessageReader(connection.getInputStream());
             OutputStream out = connection.getOutputStream()) {
//            requestReader.print();

            final String url = requestReader.readUrlPath();
            switch (url) {
                case "/user/form.html":
                case "/favicon.ico":
                case "/index.html": {
                    handleFileRequest(url, out);
                    break;
                }
                case "/user/create": {
                    handleSignUp(requestReader.getQueryParams(), out);
                }
                default: {
                    handleDefault(out);
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void handleDefault(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = "Hello World".getBytes();
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void handleFileRequest(String url, OutputStream out) throws IOException {
        try {
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos,body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IOException();
        }
    }

    private void handleSignUp(Map<String, String> queryParams, OutputStream out) throws IOException {
        final String userId = queryParams.get("userId");
        final String password = queryParams.get("password");
        final String name = queryParams.get("name");
        final String email = queryParams.get("email");
        User user = new User(userId, password, name, email);

        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = user.toString().getBytes();
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
