package http;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private static final Map<String, HttpMethod> MAP = Stream.of(values()).collect(Collectors.toMap(HttpMethod::getValue, Function.identity()));

    private final String value;
    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod valueToMethod(String value) {
        return MAP.get(value);
    }

    public String getValue() {
        return value;
    }
}
