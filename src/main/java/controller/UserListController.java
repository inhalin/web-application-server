package controller;

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

// http://localhost:8080/user/list
public class UserListController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final UserService userService;

    public UserListController() {
        this.userService = new UserService();
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        return methodMapping(request);
    }

    @Override
    public HttpResponse get(HttpRequest request) {
        String isLogined = request.getHeader().getCookie().getOrDefault("logined", "false");
        if ("false".equals(isLogined)) {
            Map<HttpHeaders, String> map = Map.ofEntries(
                    entry(CONTENT_TYPE, "text/html; charset=utf-8"),
                    entry(CONTENT_LENGTH, "0"),
                    entry(LOCATION, "/index.html")
            );
            return HttpResponse.of(HttpBody.of(""), HttpResponseHeader.from(map), StatusCode.FOUND);
        }

        String body = userService.getList();

        Map<HttpHeaders, String> map = Map.ofEntries(
                entry(CONTENT_TYPE, "text/html; charset=utf-8"),
                entry(CONTENT_LENGTH, String.valueOf(body.getBytes().length))
        );
        return HttpResponse.of(HttpBody.of(body), HttpResponseHeader.from(map), StatusCode.OK);
    }

    @Override
    public HttpResponse post(HttpRequest request) {
        Map<HttpHeaders, String> map = Map.ofEntries(
                entry(CONTENT_TYPE, "text/html; charset=utf-8"),
                entry(CONTENT_LENGTH, "0")
        );

        return HttpResponse.of(HttpBody.of(""), HttpResponseHeader.from(map), StatusCode.NOT_FOUND);
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
