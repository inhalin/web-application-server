package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import static db.DataBase.*;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();
            log.debug("request = {}", line);
            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");
            String url = getUrl(tokens[1]);
            String params = getParams(tokens[1]);
            log.debug("url = {}, params = {}", url, params);

            int contentLength = 0;
            boolean isLoggedIn = false;
            while (!"".equals(line)) {
                line = br.readLine();
                log.debug("header = {}", line);

                if (line.contains("Content-Length")) {
                    contentLength = getContentLength(line);
                    // 여기서 IOUtils.readData() 하면 안됨
                    // params = IOUtils.readData(br, contentLength);
                    // log.debug("params = {}", params); // => params = Cache-Control: max-age=0 sec-ch-ua: "Chromium";v="1
                }

                if (line.contains("Cookie")) {
                    isLoggedIn = getLoginStatus(line);
                    log.debug("isLoggedIn = {}", isLoggedIn);
                }
            }

            if (contentLength > 0) {
                params = IOUtils.readData(br, contentLength);
                log.debug("params = {}", params);
            }

            Map<String, String> userData = parseData(params);
            DataOutputStream dos = new DataOutputStream(out);

            if (url.equals("/user/list")) {
                if (!isLoggedIn) {
                    response302Header(dos, "/user/login.html");
                }

                Collection<User> users = findAll();
                StringBuilder sb = new StringBuilder();
                for (User user : users) {
                    sb.append("<p>아이디: ").append(user.getUserId()).append("</p><br>");
                    sb.append("<p>이름: ").append(user.getName()).append("</p><br>");
                    sb.append("<p>이메일: ").append(user.getEmail()).append("</p><br>");
                }
                byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            if (url.equals("/user/login")) {
                String userId = userData.get("userId");
                String password = userData.get("password");
                User user = findUserById(userId);
                if (user == null) {
                    response302Header(dos, "/user/login_failed.html");
                    return;
                }

                boolean loggedIn = password.equals(user.getPassword());
                if (loggedIn) {
                    responseLoggedInHeader(dos, "/index.html");
                } else {
                    response302Header(dos, "/user/login_failed.html");
                }
            }

            if (url.equals("/user/create")) {
                User user = new User(userData.get("userId"), userData.get("password"), userData.get("name"), userData.get("email"));
                addUser(user);
                response302Header(dos, "/index.html");
            }

            if (url.endsWith(".css")) {
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200CssHeader(dos, body.length);
                responseBody(dos, body);
            }

            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseLoggedInHeader(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("Set-Cookie: loggedin=true\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
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

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
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

    private String getUrl(String token) {
        String url = token;

        if (url.contains("?")) {
            int index = url.indexOf("?");
            url = url.substring(0, index);
        }

        return url;
    }

    private String getParams(String token) {
        if (token.contains("?")) {
            int index = token.indexOf("?") + 1;
            return token.substring(index);
        }

        return null;
    }

    private int getContentLength(String line) {
        return Integer.parseInt(line.split(":")[1].trim());
    }

    private Map<String, String> parseData(String data) {
        return HttpRequestUtils.parseQueryString(data);
    }

    private boolean getLoginStatus(String line) {
        String cookie = line.split(":")[1].trim();
        log.debug("cookie = {}", cookie);

        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookie);

        boolean loggedIn = Boolean.parseBoolean(cookies.get("loggedin"));
        log.debug("loggedIn = {}", loggedIn);

        return loggedIn;
    }
}
