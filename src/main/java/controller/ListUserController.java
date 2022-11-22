package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSessions;
import model.User;
import util.HttpRequestUtils;

import java.util.Collection;
import java.util.Map;

public class ListUserController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        boolean isLoginResult = isLogin(request.getHeader("Cookie"));

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

            response.forwardBody(sb.toString());
        } else {
            response.sendRedirect("/user/login.html");
        }
    }
    public boolean isLogin(String cookies) {
        Map<String, String> cookieMap = HttpRequestUtils.parseCookies(cookies);

        return HttpSessions.getSession(cookieMap.get("JSESSIONID")) != null;
    }
}
