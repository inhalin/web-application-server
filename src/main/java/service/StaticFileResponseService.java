package service;

import http.protocol.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class StaticFileResponseService extends ResponseService {

    private static final String BASE_PATH = System.getProperty("user.dir") + "/webapp";

    public StaticFileResponseService(HttpRequest req) {
        super(req);
    }

    @Override
    public HttpResponse serve() throws IOException {
        String url = super.req.getUrl();

        byte[] bodyByte = getBody(url);

        HttpHeaders headers = getHeader(bodyByte, url);

        return new HttpResponse.Builder(HttpStatusCode.OK, HttpVersion.HTTP11, headers)
                .body(bodyByte)
                .build();
    }

    private HttpHeaders getHeader(byte[] body, String url) {
        ContentType contentType = getContentType(url);

        Map<HttpHeader, String> headerMap = Map.of(
                HttpHeader.CONTENT_TYPE, contentType.getValue(),
                HttpHeader.CONTENT_LENGTH, String.valueOf(body.length));

        return new HttpHeaders(headerMap);
    }

    private ContentType getContentType(String url) {
        int index = url.lastIndexOf(".");
        String fileExtension = url.substring(index + 1);
        return ContentType.find(fileExtension);
    }

    private byte[] getBody(String url) throws IOException {
        Path filePath = new File(BASE_PATH + url).toPath();
        return Files.readAllBytes(filePath);
    }
}
