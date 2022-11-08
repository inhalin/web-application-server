package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.util.Collection;

public class ListUserController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        boolean isLoginResult = isLogin(request.getHeader("logined"));

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
    public boolean isLogin(String isLogin) {
        return Boolean.parseBoolean(isLogin);
    }
}
