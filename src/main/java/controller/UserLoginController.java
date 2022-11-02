package controller;

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

// http://localhost:8080/user/login
public class UserLoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final UserService userService;

    public UserLoginController() {
        this.userService = new UserService();
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return methodMapping(request);
    }

    @Override
    public HttpResponse get(HttpRequest request) {
        Map<HttpHeaders, String> map = Map.ofEntries(
                entry(CONTENT_TYPE, "text/html; charset=utf-8"),
                entry(CONTENT_LENGTH, "0")
        );

        return HttpResponse.of(HttpBody.of(""), HttpResponseHeader.from(map), StatusCode.NOT_FOUND);
    }

    @Override
    public HttpResponse post(HttpRequest request) {
        Map<String, String> body = HttpRequestUtils.parseQueryString(request.getBody().getBody());
        String userId = body.getOrDefault("userId", "");
        String password = body.getOrDefault("password", "");

        String location = "/user/login_failed.html";
        String setCookie = "logined=false";
        boolean isSuccess = userService.login(userId, password);

        if (isSuccess) {
            location = "/index.html";
            setCookie = "logined=true";
            log.info("login success!!");
        }

        Map<HttpHeaders, String> map = Map.ofEntries(
                entry(CONTENT_TYPE, "text/html; charset=utf-8"),
                entry(CONTENT_LENGTH, "0"),
                entry(LOCATION, location),
                entry(SET_COOKIES, setCookie)
        );
        return HttpResponse.of(HttpBody.of(""), HttpResponseHeader.from(map), StatusCode.FOUND);
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
