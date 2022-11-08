package service;

import db.DataBase;
import http.protocol.*;
import model.User;
import util.HttpRequestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import static webserver.RequestHandler.FILE_BASE_PATH;

public class GetUserListService extends ResponseService {
    public GetUserListService(HttpRequest req) {
        super(req);
    }

    @Override
    public HttpResponse serve() throws IOException {
        if (!loggedIn()) {
            return responseLoginPage();
        }
        return responseUserListPage();
    }

    private HttpResponse responseUserListPage() {
        Collection<User> users = DataBase.findAll();
        String body = buildUsersHtml(users);
        Map<HttpHeader, String> header = userListPageHeader(body.length());
        return new HttpResponse.Builder(HttpStatusCode.OK, HttpVersion.HTTP11, new HttpHeaders(header))
                .body(body.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    private Map<HttpHeader, String> userListPageHeader(int length) {
        Map<HttpHeader, String> headerMap = Map.of(
                HttpHeader.CONTENT_TYPE, ContentType.HTML.getValue(),
                HttpHeader.CONTENT_LENGTH, String.valueOf(length));
        return headerMap;
    }

    private String buildUsersHtml(Collection<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head></head>");
        sb.append("<body>");
        sb.append("<table class=\"table table-hover\">\n");
        sb.append("<thead>\n");
        sb.append("<tr>\n");
        sb.append("<th>#</th> <th>사용자 아이디</th> <th>이름</th> <th>이메일</th><th></th>\n");
        sb.append("</tr>\n");
        sb.append("</thead>\n");
        sb.append("<tbody>\n");
        for (User user : users) {
            sb.append("<tr>\n");
            sb.append("<th scope=\"row\">1</th>");
            sb.append(" <td>").append(user.getUserId()).append("</td>");
            sb.append(" <td>").append(user.getName()).append("</td>");
            sb.append(" <td>").append(user.getEmail()).append("</td><td>");
            sb.append("</tr>\n");
        }
        sb.append("</tbody>\n");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    private HttpResponse responseLoginPage() throws IOException {
        byte[] body = getFile("/user/login.html");
        Map<HttpHeader, String> headerMap = loginPageHeader(body.length);
        return new HttpResponse.Builder(HttpStatusCode.OK, HttpVersion.HTTP11, new HttpHeaders(headerMap))
                .body(body)
                .build();
    }

    private Map<HttpHeader, String> loginPageHeader(int length) {
        Map<HttpHeader, String> headerMap = Map.of(
                HttpHeader.CONTENT_TYPE, ContentType.HTML.getValue(),
                HttpHeader.CONTENT_LENGTH, String.valueOf(length));
        return headerMap;
    }

    private byte[] getFile(String path) throws IOException {
        Path p = new File(FILE_BASE_PATH + path).toPath();
        return Files.readAllBytes(p);
    }

    private Boolean loggedIn() {
        String cookie = req.getCookie();
        Map<String, String> cookieMap = HttpRequestUtils.parseCookies(cookie);
        return Boolean.valueOf(cookieMap.get("logined"));
    }
}
