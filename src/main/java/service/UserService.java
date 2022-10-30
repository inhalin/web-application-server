package service;

import db.DataBase;
import model.User;

import java.util.Collection;
import java.util.Optional;

public class UserService {

    public User create(String userId, String password, String name, String email) {
        User user = new User(userId, password, name, email);
        DataBase.addUser(user);

        return user;
    }

    public boolean login(String userId, String password) {
        return Optional.ofNullable(DataBase.findUserById(userId))
                .filter(s -> s.getPassword().equals(password))
                .isPresent();
    }

    public String getList() {
        StringBuilder userList = new StringBuilder();
        Collection<User> users = DataBase.findAll();
        userList.append("<table class=\"table table-hover\">\n");
        userList.append("<thead>\n");
        userList.append("<tr>\n");
        userList.append("<th>#</th> <th>사용자 아이디</th> <th>이름</th> <th>이메일</th><th></th>\n");
        userList.append("</tr>\n");
        userList.append("</thead>\n");
        userList.append("<tbody>\n");
        for (User user : users) {
            userList.append("<tr>\n");
            userList.append("<th scope=\"row\">1</th>");
            userList.append(" <td>").append(user.getUserId()).append("</td>");
            userList.append(" <td>").append(user.getName()).append("</td>");
            userList.append(" <td>").append(user.getEmail()).append("</td><td>");
            userList.append("</tr>\n");
        }
        userList.append("</tbody>\n");
        userList.append("</table>");

        return userList.toString();
    }
}
