package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CustomUtils;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line = br.readLine();
            if (line == null) {
                return;
            }
            Map<String, String> requestPath = CustomUtils.getRequestPath(line);

//            while (true) {
//                line = br.readLine();
//                if (line == null) {
//                    return;
//                }
//                if ("".equals(line)) {
//                    log.debug("header empty : {}", line);
//                    break;
//                }
//                log.debug("header : {}", line);
//            }

            if ("/index.html".equals(requestPath.get("path"))
                    || "/user/form.html".equals(requestPath.get("path"))
                    || "/user/login.html".equals(requestPath.get("path"))) {
                byte[] body = Files.readAllBytes(new File("./webapp" + requestPath.get("path")).toPath());
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else if ("/user/create".equals(requestPath.get("path"))) {
                int contentLength = Integer.parseInt(CustomUtils.getHeaderValue(br, "Content-Length"));
                CustomUtils.moveEndLine(br);

                Map<String, String> requestBody = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
                User user = new User(requestBody.get("userId"), requestBody.get("password"), requestBody.get("name"), requestBody.get("email"));
                log.info("{}", user);

                DataBase.addUser(user);

                byte[] responseBody = Files.readAllBytes(new File("./webapp/index.html").toPath());
                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, responseBody.length, "/index.html");
                responseBody(dos, responseBody);
            } else if ("/user/login".equals(requestPath.get("path"))) {
                int contentLength = Integer.parseInt(CustomUtils.getHeaderValue(br, "Content-Length"));
                CustomUtils.moveEndLine(br);

                Map<String, String> requestBody = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
                User loginUser = new User(requestBody.get("userId"), requestBody.get("password"));
                log.info("{}", loginUser);

                User findUser = DataBase.findUserById(loginUser.getUserId());

                if (loginUser.isLoginOK(findUser)) {
                    byte[] responseBody = Files.readAllBytes(new File("./webapp/index.html").toPath());
                    DataOutputStream dos = new DataOutputStream(out);
                    response302HeaderWithCookie(dos, responseBody.length, "/index.html", true);
                    responseBody(dos, responseBody);
                } else {
                    byte[] responseBody = Files.readAllBytes(new File("./webapp/user/login_failed.html").toPath());
                    DataOutputStream dos = new DataOutputStream(out);
                    response302HeaderWithCookie(dos, responseBody.length, "/user/login_failed.html", false);
                    responseBody(dos, responseBody);
                }
            } else if ("/user/list".equals(requestPath.get("path"))) {
                Map<String, String> cookies = HttpRequestUtils.parseCookies("Cookie");

                boolean isLoginResult = Boolean.parseBoolean(cookies.get("logined"));
                
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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

    private void response302Header(DataOutputStream dos, int lengthOfBodyContent, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Temporary Redirect \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    // TODO 리팩토링 필요
    private void response302HeaderWithCookie(DataOutputStream dos, int lengthOfBodyContent, String redirectUrl, boolean isLoginResult) {
        try {
            dos.writeBytes("HTTP/1.1 302 Temporary Redirect \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Location: " + redirectUrl + "\r\n");
            dos.writeBytes("Set-Cookie: logined=" + isLoginResult + "\r\n");
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

    // TODO 리팩토링 필요
    private void responseEntity() {

    }
}
