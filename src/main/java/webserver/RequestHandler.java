package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import io.HttpMessageReader;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    public static final String FILE_BASE_PATH = "./webapp";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (HttpMessageReader requestReader = new HttpMessageReader(connection.getInputStream());
             OutputStream out = connection.getOutputStream()) {

            final String url = requestReader.readUrlPath();
            switch (url) {
                case "/css/style.css":
                case "/css/bootstrap.min.css": {
                       handleCssFileRequest(url, new DataOutputStream(out));
                       break;
                }
                case "/user/login.html":
                case "/user/form.html":
                case "/favicon.ico":
                case "/index.html": {
                    handleFileRequest(url, out);
                    break;
                }
                case "/user/create": {
                    handleSignUp(requestReader.readBodyMap(), out);
                    break;
                }
                case "/user/login": {
                    handleLogin(requestReader.readBodyMap(), out);
                    break;
                }
                case "/user/list": {
                    handleUserList(requestReader.getHeaders(), out);
                    break;
                }
                default: {
                    handleDefault(out);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void handleCssFileRequest(String path, DataOutputStream dos) throws IOException {
        byte[] body = Files.readAllBytes(new File(FILE_BASE_PATH + path).toPath());
        responseCssFile200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void responseCssFile200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void handleUserList(Map<String, String> headers, OutputStream out) throws IOException {
        boolean logined = isLogined(headers);

        DataOutputStream dos = new DataOutputStream(out);
        if (logined) {
            Collection<User> users = DataBase.findAll();
            String body = buildUsersHtml(users);
            response200HeaderWithCookie(dos, body.length(), logined);
            responseBody(dos, body.getBytes());
            return;
        }

        byte[] file = Files.readAllBytes(new File(FILE_BASE_PATH + "/user/login.html").toPath());
        response200HeaderWithCookie(dos, file.length, logined);
        responseBody(dos, file);
    }

    private String buildUsersHtml(Collection<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head></head>");
        sb.append("<body>");
        for (User user : users) {
            sb.append(user.toString()).append('\n');
        }
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    private boolean isLogined(Map<String, String> headers) throws IOException {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headers.get("Cookie"));
        return Boolean.parseBoolean(cookies.get("logined"));
    }

    private void handleLogin(Map<String, String> body, OutputStream out) throws IOException {
        String userId = body.get("userId");
        User user = DataBase.findUserById(userId);

        DataOutputStream dos = new DataOutputStream(out);
        if (user == null) {
            byte[] file = Files.readAllBytes(new File(FILE_BASE_PATH + "/user/login_failed.html").toPath());
            response200HeaderWithCookie(dos, file.length, false);
            responseBody(dos, file);
            return;
        }

        byte[] file = Files.readAllBytes(new File(FILE_BASE_PATH + "/index.html").toPath());
        response200HeaderWithCookie(dos, file.length, true);
        responseBody(dos, file);
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
            byte[] body = Files.readAllBytes(new File(FILE_BASE_PATH + url).toPath());
            response200Header(dos,body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IOException();
        }
    }

    private void handleSignUp(Map<String, String> queryParams, OutputStream out) throws IOException {
        createUser(queryParams);
        response302Header(new DataOutputStream(out));
    }

    private void createUser(Map<String, String> queryParams) {
        final String userId = queryParams.get("userId");
        final String password = queryParams.get("password");
        final String name = queryParams.get("name");
        final String email = queryParams.get("email");
        User user = new User(userId, password, name, email);
        DataBase.addUser(user);
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

    // Cookie 클래스 정의하면 좋을듯.
    private void response200HeaderWithCookie(DataOutputStream dos, int lengthOfBodyContent, boolean cookie) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-Cookie: logined=" + cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: http://localhost:8080/index.html\r\n");
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
