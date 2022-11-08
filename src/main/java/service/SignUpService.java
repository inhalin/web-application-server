package service;

import db.DataBase;
import http.protocol.*;
import model.User;

import java.util.Map;

public class SignUpService extends ResponseService {
    public SignUpService(HttpRequest req) {
        super(req);
    }

    @Override
    public HttpResponse serve() {
        createUser(req.getBody());

        Map<HttpHeader, String> header = Map.of(HttpHeader.LOCATION, "http://localhost:8080/index.html");
        HttpHeaders headers = new HttpHeaders(header);

        return new HttpResponse.Builder(HttpStatusCode.FOUND, HttpVersion.HTTP11, headers)
                .build();
    }

    private void createUser(Map<String, String> formData) {
        final String userId = formData.get("userId");
        final String password = formData.get("password");
        final String name = formData.get("name");
        final String email = formData.get("email");
        User user = new User(userId, password, name, email);
        DataBase.addUser(user);
    }
}
