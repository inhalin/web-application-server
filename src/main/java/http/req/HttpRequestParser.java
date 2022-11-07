package http.req;

import http.protocol.*;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    private final BufferedReader messageReader;
    private final HttpRequest.Builder reqBuilder;
    private int contentLength;

    public HttpRequestParser(InputStream in) throws IOException {
        this.messageReader = new BufferedReader(new InputStreamReader(in));
        this.reqBuilder = new HttpRequest.Builder();
    }

    public HttpRequest parse() throws IOException {
        parseFirstLine();
        parseHeader();
        parseBody();
        return reqBuilder.build();
    }

    private void parseFirstLine() throws IOException {
        String[] line = messageReader.readLine().split(" ");

        HttpMethod method = HttpMethod.find(line[0]);
        String url = line[1];
        HttpVersion httpVersion = HttpVersion.find(line[2]);

        reqBuilder.method(method)
                .url(url)
                .version(httpVersion);
    }

    private void parseHeader() throws IOException {
        Map<HttpHeader, String> headers = new HashMap<>();

        do {
            String line = messageReader.readLine();
            if (line == null || line.equals("")) break;

            int index = line.indexOf(":");
            HttpHeader key = HttpHeader.find(line.substring(0, index));
            String value = line.substring(index + 1).strip();

            if (key == HttpHeader.NONE) continue;
            if (key == HttpHeader.CONTENT_LENGTH) contentLength = Integer.parseInt(value);
            headers.put(key, value);
        } while (true);

        reqBuilder.headers(new HttpHeaders(headers));
    }

    private void parseBody() throws IOException {
        String body = IOUtils.readData(messageReader, contentLength);
        Map<String, String> bodyMap = HttpRequestUtils.parseQueryString(body);
        reqBuilder.body(bodyMap);
    }
}
