package service;

import http.protocol.HttpMethod;
import http.protocol.HttpRequest;

public class ResponseServiceFactory {
    public static ResponseService create(HttpRequest req) {
        final String url = req.getUrl();
        final HttpMethod method = req.getMethod();

        if (isStaticFileRequest(url)) {
            return new StaticFileService(req);
        }

        switch (url) {
            case "/user/create" -> {
                if (method == HttpMethod.POST) {
                    return new SignUpService(req);
                }
            }
            case "/user/login" -> {
                if (method == HttpMethod.POST) {
                    return new LoginService(req);
                }
            }
            case "/user/list" -> {
                if (method == HttpMethod.GET) {
                    return new GetUserListService(req);
                }
            }

        }

        return new NotFoundService(req);
    }

    private static boolean isStaticFileRequest(String url) {
        return url.contains(".");
    }
}
