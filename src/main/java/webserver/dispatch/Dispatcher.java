package webserver.dispatch;

import controller.UserListController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;
import webserver.dispatch.controller.Controller;
import controller.UserCreateController;
import controller.UserLoginController;
import webserver.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;
import static webserver.http.HttpHeaders.CONTENT_LENGTH;
import static webserver.http.HttpHeaders.CONTENT_TYPE;

public class Dispatcher {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static final Map<String, Controller> requestMappingValue;

    /*
     * 각 URL 마다 고유한 컨트롤러 객체를 생성한다.
     * */
    static {
        requestMappingValue = Map.ofEntries(
                entry("/user/create", new UserCreateController()),
                entry("/user/login", new UserLoginController()),
                entry("/user/list", new UserListController())
        );
    }

    /**
     * Return : HttpResponse 객체
     *
     * 1. 만약 정적 리소스 요청이면 handleStaticResource() 메서드에서
     *    요청한 리소스 파일을 byte[]로 읽어서 HttpBody 객체에 담는다.
     * 2. 정적 리소스가 아니면 handlerMapping() 메서드가 리퀘스트 요청 path를 참조해서 컨트롤러 객체를 찾는다.
     * 3. 찾은 컨트롤러 객체의 handle() 메서드 호출해서 로직 실행
     * */
    public static HttpResponse dispatch(HttpRequest request) throws IOException {
        log.info("start dispatch, method : {}, url : {}", request.getMethod(), request.getUrl());

        if (isStaticResource(request.getPath())) {
            return handleStaticResource(request);
        }

        Controller controller = controllerMapping(request.getPath());
        return controller.handle(request);
    }

    public static boolean isStaticResource(String path) {
        return requestMappingValue.entrySet().stream()
                .noneMatch(s -> s.getKey().equals(path));
    }

    private static HttpResponse handleStaticResource(HttpRequest request) throws IOException {
        String path = request.getPath();
        String contentType = getContentType(request);

        if ("/".equals(path)) {
            path += "index.html";
        }

        byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
        Map<HttpHeaders, String> map = Map.ofEntries(
                entry(CONTENT_TYPE, contentType),
                entry(CONTENT_LENGTH, String.valueOf(body.length))
        );
        return HttpResponse.of(HttpBody.of(body), HttpResponseHeader.from(map), StatusCode.OK);
    }

    private static String getContentType(HttpRequest request) {
        String accept = request.getHeader().getAccept();
        String contentType = "text/html; charset=utf-8";

        if (accept.contains("css")) {
            contentType = "text/css; charset=utf-8";
        }
        return contentType;
    }

    private static Controller controllerMapping(String requestPath) {
        for (String path : requestMappingValue.keySet()) {
            if (path.equals(requestPath)) {
                return requestMappingValue.get(path);
            }
        }
        throw new IllegalStateException("올바른 경로가 아닙니다");
    }
}
