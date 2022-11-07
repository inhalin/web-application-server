package http.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    NONE("NONE");

    private static final Map<String, HttpMethod> MAP = Arrays.stream(values())
            .collect(Collectors.toMap(HttpMethod::getValue, Function.identity()));

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod find(String value) {
        return MAP.get(value);
    }

    public String getValue() {
        return value;
    }
}
