package webserver;

import java.io.*;
import java.net.Socket;

import http.protocol.HttpRequest;
import http.protocol.HttpResponse;
import http.req.HttpRequestParser;
import http.res.HttpResponseWriter;
import service.ResponseService;
import service.ResponseServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    public static final String FILE_BASE_PATH = "./webapp";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest req = new HttpRequestParser(in).parse();
            ResponseService service = ResponseServiceFactory.create(req);
            HttpResponse response = service.serve();
            new HttpResponseWriter(out, response).write();
        } catch (IOException e) {
            log.error("exception : e {}", e);
        }
    }
}
