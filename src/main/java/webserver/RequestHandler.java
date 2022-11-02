package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CustomUtils;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
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
            Map<String, String> startLineInfo = CustomUtils.getStartLine(line);
            String path = startLineInfo.get("path");

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

            if ("/index.html".equals(path)
                    || "/user/form.html".equals(path)
                    || "/user/login.html".equals(path)) {
                byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
                DataOutputStream dos = new DataOutputStream(out);
                responseHeader(dos, body.length, HttpURLConnection.HTTP_OK, "text/html", null);
                responseBody(dos, body);
            } else if ("/user/create".equals(path)) {
                int contentLength = Integer.parseInt(CustomUtils.getHeaderValue(br, "Content-Length"));
                CustomUtils.moveEndLine(br);

                Map<String, String> requestBody = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
                User user = new User(requestBody.get("userId"), requestBody.get("password"), requestBody.get("name"), requestBody.get("email"));
                log.info("{}", user);

                DataBase.addUser(user);

                byte[] responseBody = Files.readAllBytes(new File("./webapp/index.html").toPath());
                DataOutputStream dos = new DataOutputStream(out);
                responseHeader(dos, responseBody.length, HttpURLConnection.HTTP_MOVED_TEMP, "text/html", Map.of("Location", "/index.html"));
                responseBody(dos, responseBody);
            } else if ("/user/login".equals(path)) {
                int contentLength = Integer.parseInt(CustomUtils.getHeaderValue(br, "Content-Length"));
                CustomUtils.moveEndLine(br);

                Map<String, String> requestBody = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
                User loginUser = new User(requestBody.get("userId"), requestBody.get("password"));
                log.info("{}", loginUser);

                User findUser = DataBase.findUserById(loginUser.getUserId());

                // TODO 쿠키 해당 경로에 안들어가는 이슈
                if (loginUser.isLoginOK(findUser)) {
                    byte[] responseBody = Files.readAllBytes(new File("./webapp/index.html").toPath());
                    DataOutputStream dos = new DataOutputStream(out);
                    responseHeader(dos, responseBody.length, HttpURLConnection.HTTP_MOVED_TEMP, "text/html",
                            Map.of("Location", "/index.html", "Set-Cookie", "logined=true"));
                    responseBody(dos, responseBody);
                } else {
                    byte[] responseBody = Files.readAllBytes(new File("./webapp/user/login_failed.html").toPath());
                    DataOutputStream dos = new DataOutputStream(out);
                    responseHeader(dos, responseBody.length, HttpURLConnection.HTTP_MOVED_TEMP, "text/html",
                            Map.of("Location", "/user/login_failed.html", "Set-Cookie", "logined=false"));
                    responseBody(dos, responseBody);
                }
            } else if ("/user/list".equals(path)) {
                Map<String, String> cookies = HttpRequestUtils.parseCookies(CustomUtils.getHeaderValue(br, "Cookie"));

                boolean isLoginResult = Boolean.parseBoolean(cookies.get("logined"));

                if (isLoginResult) {
                    Collection<User> users = DataBase.findAll();
                    StringBuilder sb = new StringBuilder();

                    sb.append("<html>\n");
                    sb.append("<head>\n");
                    sb.append("</head>\n");
                    sb.append("<body>\n");

                    users.forEach(user -> {
                        sb.append("<div>" + user.getUserId() + "</div>\n");
                    });

                    sb.append("</body>\n");
                    sb.append("</html>");

                    byte[] responseBody = sb.toString().getBytes();
                    DataOutputStream dos = new DataOutputStream(out);
                    responseHeader(dos, responseBody.length, HttpURLConnection.HTTP_OK, "text/html", null);
                    responseBody(dos, responseBody);
                } else {
                    byte[] responseBody = Files.readAllBytes(new File("./webapp/user/login.html").toPath());
                    DataOutputStream dos = new DataOutputStream(out);
                    responseHeader(dos, responseBody.length, HttpURLConnection.HTTP_MOVED_TEMP, "text/html",
                            Map.of("Location", "/user/login.html"));
                    responseBody(dos, responseBody);
                }
            } else if (CustomUtils.isStaticResource(path)) {
                byte[] responseBody = Files.readAllBytes(new File("./webapp" + path).toPath());
                DataOutputStream dos = new DataOutputStream(out);
                responseHeader(dos, responseBody.length, HttpURLConnection.HTTP_MOVED_TEMP, "text/css", null);
                responseBody(dos, responseBody);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseHeader(DataOutputStream dos, int lengthOfBodyContent, int statusCode, String contentType, Map<String, String> headers) {
        try {
            switch (statusCode) {
                case 200:
                    dos.writeBytes("HTTP/1.1 200 OK \r\n");
                    break;
                case 302:
                    dos.writeBytes("HTTP/1.1 302 Temporary Redirect \r\n");
                    break;
                default:
                    break;
            }

            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
                }
            }

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
