package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import http.HttpSessions;
import model.User;

public class LoginController extends AbstractController {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User loginUser = new User(request.getParameter("userId"), request.getParameter("password"));
        User findUser = DataBase.findUserById(loginUser.getUserId());

        if (loginUser.isLoginOK(findUser)) {
            HttpSession session = HttpSessions.createSession();

            response.addHeader("Set-Cookie", "JSESSIONID="+session.getId()+"; Path=/");
            response.sendRedirect("/index.html");
        } else {
            response.addHeader("Set-Cookie", "Path=/");
            response.sendRedirect("/user/login_failed.html");
        }
    }
}
