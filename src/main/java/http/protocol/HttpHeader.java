package http.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIES("Cookie"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    ACCEPT("Accept"),
    NONE("NONE");

    private static final Map<String, HttpHeader> MAP = Arrays.stream(values())
            .collect(Collectors.toMap(HttpHeader::getValue, Function.identity()));

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public static HttpHeader find(String value) {
        return MAP.get(value);
    }

    public String getValue() {
        return value;
    }

}
