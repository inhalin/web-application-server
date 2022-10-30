package webserver.http;

public enum Method {

    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    OPTIONS,
    HEAD,
    TRACE,
    CONNECT,
    NONE;

    public static Method match(String target) {
        for (Method method : Method.values()) {
            if (method.name().equals(target)) {
                return method;
            }
        }
        return NONE;
    }
}
