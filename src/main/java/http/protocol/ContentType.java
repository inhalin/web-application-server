package http.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ContentType {
    CSS("css", "text/css;"),
    HTML("html", "text/html;charset=utf-8"),
    JS("js", "text/javascript;"),
    ICO("ico", "image/x-icon"),
    NONE("NONE","NONE");

    private static final Map<String, ContentType> MAP = Arrays.stream(values())
            .collect(Collectors.toMap(ContentType::getExt, Function.identity()));

    private final String ext;
    private final String value;

    ContentType(String ext, String value) {
        this.ext = ext;
        this.value = value;
    }

    public String getExt() {
        return ext;
    }

    public String getValue() {
        return value;
    }

    public static ContentType find(String ext) {
        return MAP.get(ext);
    }

}
