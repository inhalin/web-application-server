package webserver.dispatch.controller;

import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

public interface Controller {

    HttpResponse handle(HttpRequest request);
    HttpResponse get(HttpRequest request);
    HttpResponse post(HttpRequest request);
    HttpResponse put(HttpRequest request);
    HttpResponse delete(HttpRequest request);
    HttpResponse patch(HttpRequest request);

    default HttpResponse methodMapping(HttpRequest request) {
        switch (request.getMethod()) {
            case POST: return post(request);
            case PUT: return put(request);
            case DELETE: return delete(request);
            case PATCH: return patch(request);
            default: return get(request);
        }
    }
}
