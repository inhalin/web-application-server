package http;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private String value;

    HttpMethod(String value) {
        this.value = value;
    }
}
