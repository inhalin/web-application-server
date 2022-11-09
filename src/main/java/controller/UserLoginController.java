package controller;

import model.User;
import util.HttpRequest;
import util.HttpResponse;

import static db.DataBase.findUserById;

public class UserLoginController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
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
    }
}
