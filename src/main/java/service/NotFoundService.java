package service;

import http.protocol.*;

import java.util.Map;

public class NotFoundService extends ResponseService {

    public NotFoundService(HttpRequest req) {
        super(req);
    }

    @Override
    public HttpResponse serve() {
        byte[] body = "Not Found".getBytes();

        Map<HttpHeader, String> header = Map.of(
                HttpHeader.CONTENT_TYPE, ContentType.HTML.getValue(),
                HttpHeader.CONTENT_LENGTH, String.valueOf(body.length));

        return new HttpResponse.Builder(HttpStatusCode.OK, HttpVersion.HTTP11, new HttpHeaders(header))
                .body(body)
                .build();
    }
}
