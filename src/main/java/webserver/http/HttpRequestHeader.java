package webserver.http;

import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private String contentType;
    private int contentLength;
    private Map<String, String> cookie;
    private String accept;

    protected HttpRequestHeader() {
        this.contentType = "";
        this.contentLength = 0;
        this.cookie = new HashMap<>();
        this.accept = "";
    }

    public static HttpRequestHeader from(Map<HttpHeaders, String> map) {
        HttpRequestHeader header = new HttpRequestHeader();

        map.forEach((key, value) -> {
            switch (key) {
                case CONTENT_LENGTH: header.setContentLength(Integer.parseInt(value)); break;
                case CONTENT_TYPE: header.setContentType(value); break;
                case COOKIES: header.setCookie(HttpRequestUtils.parseCookies(value)); break;
                case ACCEPT: header.setAccept(value); break;
            }
        });

        return header;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public Map<String, String> getCookie() {
        return cookie;
    }

    public String getAccept() {
        return accept;
    }

    private void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    private void setCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    private void setAccept(String accept) {
        this.accept = accept;
    }
}
