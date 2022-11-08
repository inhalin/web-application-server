package service;

import db.DataBase;
import http.protocol.*;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static webserver.RequestHandler.FILE_BASE_PATH;

public class LoginService extends ResponseService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public LoginService(HttpRequest req) {
        super(req);
    }

    @Override
    public HttpResponse serve() throws IOException {
        if (!canLogin()) {
            return getLoginFailedResponse();
        }

        log.info("Logged in");
        return getLoginResponse();
    }

    private boolean canLogin() {
        Map<String, String> formData = req.getBody();
        String userId = formData.get("userId");
        String password = formData.get("password");
        User user = DataBase.findUserById(userId);

        if (user == null) return false;
        return user.login(password);
    }

    private HttpResponse getLoginFailedResponse() throws IOException {
        byte[] body = getFile("/user/login_failed.html");
        HttpHeaders header = getLoginFailedResponseHeader(body.length, false);
        return new HttpResponse.Builder(HttpStatusCode.OK, HttpVersion.HTTP11, header)
                .body(body)
                .build();
    }

    private HttpResponse getLoginResponse() {
        HttpHeaders header = getLoginResponseHeader();
        return new HttpResponse.Builder(HttpStatusCode.FOUND, HttpVersion.HTTP11, header)
                .build();
    }

    private HttpHeaders getLoginResponseHeader() {
        Map<HttpHeader, String> headerMap = Map.of(
                HttpHeader.SET_COOKIE, getLoginedString(true),
                HttpHeader.LOCATION, "/index.html");
        return new HttpHeaders(headerMap);
    }

    private HttpHeaders getLoginFailedResponseHeader(int length, boolean b) {
        Map<HttpHeader, String> headerMap = Map.of(
                HttpHeader.SET_COOKIE, getLoginedString(false),
                HttpHeader.CONTENT_TYPE, ContentType.HTML.getValue(),
                HttpHeader.CONTENT_LENGTH, String.valueOf(length));
        return new HttpHeaders(headerMap);
    }

    private byte[] getFile(String path) throws IOException {
        Path p = new File(FILE_BASE_PATH + path).toPath();
        return Files.readAllBytes(p);
    }

    private String getLoginedString(boolean flag) {
        return "logined=" + flag;
    }
}
