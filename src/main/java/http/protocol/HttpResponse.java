package http.protocol;

public class HttpResponse {
    private final HttpStatusCode statusCode;
    private final HttpVersion version;
    private final HttpHeaders header;
    private byte[] body;

    public HttpResponse(Builder builder) {
        this.statusCode = builder.statusCode;
        this.version = builder.version;
        this.header = builder.header;
        this.body = builder.body;
    }

    public String getFirstLine() {
        return String.format("%s %s %s \r\n", version.getValue(), statusCode.getCode(), statusCode.getText());
    }

    public String getHeaderLines() {
        return header.toString();
    }

    public byte[] getBody() {
        return body;
    }

    public static class Builder {
        private final HttpStatusCode statusCode;
        private final HttpVersion version;
        private final HttpHeaders header;
        private byte[] body;

        public Builder(HttpStatusCode statusCode, HttpVersion version, HttpHeaders header) {
            this.statusCode = statusCode;
            this.version = version;
            this.header = header;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }
    }
}
