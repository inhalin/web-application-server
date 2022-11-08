package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpRequestUtils;
import util.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            String path = getDefaultPath(request.getPath());

            if (path.equals("/user/create")) {
                User user = new User(
                        request.getParameter("userId"),
                        request.getParameter("password"),
                        request.getParameter("name"),
                        request.getParameter("email"));
                addUser(user);
                response.sendRedirect("/index.html");
            } else if (path.equals("/user/login")){
                boolean isLoggedIn = false;
                String redirectTo = "/user/login_failed.html";

                String userId = request.getParameter("userId");
                User user = findUserById(userId);

                if (user != null && user.getPassword().equals(request.getParameter("password"))) {
                    isLoggedIn = true;
                    redirectTo = "/index.html";
                }

                response.addHeader("Set-Cookie", "login=" + isLoggedIn + ";Path=/");
                response.sendRedirect(redirectTo);
            } else if (path.equals("/user/list")) {
                boolean isLogin = getLoginStatus(request.getHeader("Cookie"));

                if (isLogin) {
                    Collection<User> users = findAll();
                    StringBuilder sb = new StringBuilder();
                    for (User user : users) {
                        sb.append("<p>userId: ").append(user.getUserId()).append("</p><br>");
                        sb.append("<p>name: ").append(user.getName()).append("</p><br>");
                        sb.append("<p>email: ").append(user.getEmail()).append("</p><br>");
                    }
                    byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
                    response.forwardBody(body);
                }
                response.sendRedirect("/index.html");
            } else {
                response.forward(path);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }

    private boolean getLoginStatus(String cookie) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookie);
        return Boolean.parseBoolean(cookies.get("login"));
    }
}
