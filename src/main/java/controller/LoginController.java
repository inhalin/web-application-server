package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginController extends AbstractController {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User loginUser = new User(request.getParameter("userId"), request.getParameter("password"));
        User findUser = DataBase.findUserById(loginUser.getUserId());

        if (loginUser.isLoginOK(findUser)) {
            response.addHeader("Set-Cookie", "logined=true; Path=/");
            response.sendRedirect("/index.html");
        } else {
            response.addHeader("Set-Cookie", "logined=false; Path=/");
            response.sendRedirect("/user/login_failed.html");
        }
    }
}
