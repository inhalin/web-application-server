package http;

import util.HttpRequestUtils;

import java.util.Map;

public class HttpCookie {
    private Map<String, String> cookies;

    public HttpCookie(String cookieValues) {
        cookies = HttpRequestUtils.parseCookies(cookieValues);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
