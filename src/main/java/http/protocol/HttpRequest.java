package http.protocol;

import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final String url;
    private final HttpVersion version;
    private final HttpHeaders headers;
    private final Map<String, String> body;

    public HttpRequest(Builder builder) {
        this.method = builder.method;
        this.url = builder.url;
        this.version = builder.version;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getCookie() {
        return headers.getCookie();
    }

    public static class Builder {
        private HttpMethod method;
        private String url;
        private HttpVersion version;
        private HttpHeaders headers;
        private Map<String, String> body;

        public HttpRequest build() {
            return new HttpRequest(this);
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder version(HttpVersion version) {
            this.version = version;
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(Map<String, String> body) {
            this.body = body;
            return this;
        }
    }
}
