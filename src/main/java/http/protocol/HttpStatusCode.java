package http.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Redirect");

    private static final Map<Integer, HttpStatusCode> MAP = Arrays.stream(values())
            .collect(Collectors.toMap(HttpStatusCode::getCode, Function.identity()));

    private final int code;
    private final String text;

    HttpStatusCode(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public HttpStatusCode find(int code) {
        return MAP.get(code);
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
