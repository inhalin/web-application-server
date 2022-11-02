package webserver.http;

public class HttpResponse {

    private StatusCode statusCode;
    private HttpResponseHeader header;
    private HttpBody body;

    private HttpResponse(HttpBody body, HttpResponseHeader header, StatusCode statusCode) {
        this.body = body;
        this.header = header;
        this.statusCode = statusCode;
    }

    public static HttpResponse of(HttpBody body, HttpResponseHeader header, StatusCode statusCode) {
        return new HttpResponse(body, header, statusCode);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public HttpResponseHeader getHeader() {
        return header;
    }

    public HttpBody getBody() {
        return body;
    }
}
