package controller;

import model.User;
import util.HttpRequest;
import util.HttpResponse;

import static db.DataBase.addUser;

public class UserCreateController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email"));
        addUser(user);
        response.sendRedirect("/index.html");
    }
}
