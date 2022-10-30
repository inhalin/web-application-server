package webserver.http;

import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeader {

    private String contentType;
    private int contentLength;
    private String location;
    private Map<String, String> setCookie;

    protected HttpResponseHeader() {
        this.contentType = "";
        this.contentLength = 0;
        this.location = "";
        this.setCookie = new HashMap<>();
    }

    public static HttpResponseHeader from(Map<HttpHeaders, String> map) {
        HttpResponseHeader header = new HttpResponseHeader();

        map.forEach((key, value) -> {
            switch (key) {
                case CONTENT_LENGTH: header.setContentLength(Integer.parseInt(value)); break;
                case CONTENT_TYPE: header.setContentType(value); break;
                case SET_COOKIES: header.setSetCookie(HttpRequestUtils.parseCookies(value)); break;
                case LOCATION: header.setLocation(value); break;
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

    public String getSetCookie() {
        StringBuilder cookies = new StringBuilder();

        setCookie.forEach((k, v) -> {
            cookies.append(k).append("=").append(v).append("; ");
        });

        return cookies.toString();
    }

    public String getLocation() {
        return location;
    }

    private void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    private void setSetCookie(Map<String, String> setCookie) {
        this.setCookie = setCookie;
    }

    private void setLocation(String location) {
        this.location = location;
    }
}
