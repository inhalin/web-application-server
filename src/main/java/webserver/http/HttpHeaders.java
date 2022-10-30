package webserver.http;

public enum HttpHeaders {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIES("Cookie"),
    LOCATION("Location"),
    SET_COOKIES("Set-Cookies"),
    ACCEPT("Accept"),
    NONE("none");

    private String name;

    HttpHeaders(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HttpHeaders match(String target) {
        for (HttpHeaders header : HttpHeaders.values()) {
            if (header.getName().equals(target)) {
                return header;
            }
        }
        return NONE;
    }
}
