package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CustomUtils;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line = br.readLine();
            if (line == null) {
                return;
            }
            Map<String, String> requestPath = CustomUtils.getRequestPath(line);

//            while (true) {
//                line = br.readLine();
//                if (line == null) {
//                    return;
//                }
//                if ("".equals(line)) {
//                    log.debug("header empty : {}", line);
//                    break;
//                }
//                log.debug("header : {}", line);
//            }

            if ("/index.html".equals(requestPath.get("path")) || "/user/form.html".equals(requestPath.get("path"))) {
                byte[] body = Files.readAllBytes(new File("./webapp" + requestPath.get("path")).toPath());
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else if ("/user/create".equals(requestPath.get("path"))) {
                if ("GET".equals(requestPath.get("method"))) {
                    Map<String, String> queryString = HttpRequestUtils.parseQueryString(requestPath.get("queryString"));

                    User user = new User(queryString.get("userId"), queryString.get("password"), queryString.get("name"), queryString.get("email"));
                    System.out.println(user);
                } else if ("POST".equals(requestPath.get("method"))) {
                    int contentLength = 0;

                    while (true) {
                        line = br.readLine();
                        if (line == null) {
                            return;
                        }
                        if ("".equals(line)) {
                            break;
                        }

                        Map<String, String> headerInfo = CustomUtils.getHeaderInfo(line);

                        if (headerInfo.containsKey("Content-Length:")) {
                            contentLength = Integer.parseInt(headerInfo.get("Content-Length:"));
                        }
                    }
                    Map<String, String> body = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
                    User user = new User(body.get("userId"), body.get("password"), body.get("name"), body.get("email"));
                    System.out.println(user);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
