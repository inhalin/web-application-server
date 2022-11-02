package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import util.HttpRequestUtils;
import webserver.RequestHandler;
import webserver.dispatch.controller.Controller;
import webserver.http.*;

import java.util.Map;

import static java.util.Map.entry;
import static webserver.http.HttpHeaders.*;

// http://localhost:8080/user/create?userId=javajigi&password=password&name=JasSung&email=javajigi%40slipp.net
public class UserCreateController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final UserService userService;

    public UserCreateController() {
        this.userService = new UserService();
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return methodMapping(request);
    }

    @Override
    public HttpResponse get(HttpRequest request) {
        Map<String, String> params = request.getParams();
        String userId = params.getOrDefault("userId", "none");
        String password = params.getOrDefault("password", "none");
        String name = params.getOrDefault("name", "none");
        String email = params.getOrDefault("email", "none");

        User user = userService.create(userId, password, name, email);

        Map<HttpHeaders, String> map = Map.ofEntries(
                entry(CONTENT_TYPE, "text/html; charset=utf-8"),
                entry(CONTENT_LENGTH, String.valueOf(user.toString().getBytes().length)),
                entry(LOCATION, "/index.html")
        );
        return HttpResponse.of(HttpBody.of(user.toString()), HttpResponseHeader.from(map), StatusCode.FOUND);
    }

    @Override
    public HttpResponse post(HttpRequest request) {
        Map<String, String> body = HttpRequestUtils.parseQueryString(request.getBody().getBody());

        String userId = body.getOrDefault("userId", "none");
        String password = body.getOrDefault("password", "none");
        String name = body.getOrDefault("name", "none");
        String email = body.getOrDefault("email", "none");

        User user = userService.create(userId, password, name, email);

        Map<HttpHeaders, String> map = Map.ofEntries(
                entry(CONTENT_TYPE, "text/html; charset=utf-8"),
                entry(CONTENT_LENGTH, String.valueOf(user.toString().getBytes().length)),
                entry(LOCATION, "/index.html")
        );
        return HttpResponse.of(HttpBody.of(user.toString()), HttpResponseHeader.from(map), StatusCode.FOUND);
    }

    @Override
    public HttpResponse put(HttpRequest request) {
        Map<HttpHeaders, String> map = Map.ofEntries(
                entry(CONTENT_TYPE, "text/html; charset=utf-8"),
                entry(CONTENT_LENGTH, "0")
        );

        return HttpResponse.of(HttpBody.of(""), HttpResponseHeader.from(map), StatusCode.NOT_FOUND);
    }

    @Override
    public HttpResponse delete(HttpRequest request) {
        Map<HttpHeaders, String> map = Map.ofEntries(
                entry(CONTENT_TYPE, "text/html; charset=utf-8"),
                entry(CONTENT_LENGTH, "0")
        );

        return HttpResponse.of(HttpBody.of(""), HttpResponseHeader.from(map), StatusCode.NOT_FOUND);
    }

    @Override
    public HttpResponse patch(HttpRequest request) {
        Map<HttpHeaders, String> map = Map.ofEntries(
                entry(CONTENT_TYPE, "text/html; charset=utf-8"),
                entry(CONTENT_LENGTH, "0")
        );

        return HttpResponse.of(HttpBody.of(""), HttpResponseHeader.from(map), StatusCode.NOT_FOUND);
    }
}
