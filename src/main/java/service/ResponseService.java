package service;

import http.protocol.HttpRequest;
import http.protocol.HttpResponse;

import java.io.IOException;

public abstract class ResponseService {

    protected final HttpRequest req;

    public ResponseService(HttpRequest req) {
        this.req = req;
    }

    public abstract HttpResponse serve() throws IOException;
}
