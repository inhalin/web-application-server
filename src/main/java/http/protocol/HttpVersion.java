package http.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HttpVersion {
    HTTP11("HTTP/1.1"),
    NONE("NONE");

    private static final Map<String, HttpVersion> MAP = Arrays.stream(values())
            .collect(Collectors.toMap(HttpVersion::getValue, Function.identity()));

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion find(String value) {
        return MAP.get(value);
    }

    public String getValue() {
        return value;
    }
}
