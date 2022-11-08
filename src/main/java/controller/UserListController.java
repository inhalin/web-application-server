package controller;

import model.User;
import util.HttpRequest;
import util.HttpRequestUtils;
import util.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import static db.DataBase.findAll;

public class UserListController implements Controller{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
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
    }

    private boolean getLoginStatus(String cookie) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookie);
        return Boolean.parseBoolean(cookies.get("login"));
    }
}
