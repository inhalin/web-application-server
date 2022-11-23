package webserver;

import controller.Controller;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request = new HttpRequest(new InputStreamReader(in, "utf-8"));
            HttpResponse response = new HttpResponse(out);
            String path = request.getPath();
            Controller controller = RequestMapping.getController(path);

            if (request.getCookies().getCookie(HttpSessions.SESSION_NAME) == null) {
                response.addHeader("Set-Cookie", HttpSessions.SESSION_NAME + "=" + UUID.randomUUID() + "; Path=/");
            }

            if (controller != null) {
                controller.service(request, response);
            } else {
                response.forward(path);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
