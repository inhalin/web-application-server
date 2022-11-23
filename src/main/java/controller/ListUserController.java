package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import model.User;

import java.util.Collection;

public class ListUserController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        boolean isLoginResult = isLogin(request.getSession());

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

    private boolean isLogin(HttpSession session) {
        return session.getAttribute("user") != null;
    }
}
