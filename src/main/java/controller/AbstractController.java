package controller;

import http.HttpRequest;
import http.HttpResponse;

public abstract class AbstractController implements Controller {
    public void service(HttpRequest request, HttpResponse response) {
        switch (request.getMethod()) {
            case POST:
                doPost(request, response);
                break;
            case GET:
                doGet(request, response);
                break;
            default:
                break;
        }
    }
    protected void doPost(HttpRequest request, HttpResponse response) {

    }

    protected void doGet(HttpRequest request, HttpResponse response) {

    }
}
