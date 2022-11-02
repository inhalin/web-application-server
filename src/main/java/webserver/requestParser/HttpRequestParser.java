package webserver.requestParser;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;
import webserver.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestParser {

    public static HttpRequest parse(String message) {
        String startLine = parseStartLine(message);
        Method method = parseMethod(startLine);
        String url = parseUrl(startLine);
        String path = parsePath(url);
        Map<String, String> params = parseParams(url);

        HttpRequestHeader header = parseHeader(message);
        HttpBody body = parseBody(message);

        return HttpRequest.of(header, body, method, url, path, params);
    }

    public static HttpRequest parse(BufferedReader br) throws IOException {
        String message = readBeforeBody(br);

        String startLine = parseStartLine(message);
        Method method = parseMethod(startLine);
        String url = parseUrl(startLine);
        String path = parsePath(url);
        Map<String, String> params = parseParams(url);
        HttpRequestHeader header = parseHeader(message);
        HttpBody body = parseBody(br, header.getContentLength());

        return HttpRequest.of(header, body, method, url, path, params);

    }

    private static String readBeforeBody(BufferedReader br) throws IOException {
        StringBuilder request = new StringBuilder();

        while (br.ready()) {
            String line = br.readLine() + '\n';

            if (line.equals("\n")) {
                request.append(" ").append(line);
                return request.toString();
            }
            request.append(line);
        }

        return request.toString();
    }

    private static String parseStartLine(String message) {
        String[] eachLine = message.split("\n");
        return eachLine[0];
    }

    private static Method parseMethod(String startLine) {
        String[] startLineWords = startLine.split(" ");

        if (startLineWords.length < 1) {
            return Method.NONE;
        }
        return Method.match(startLineWords[0]);
    }

    private static String parseUrl(String startLine) {
        String[] startLineWords = startLine.split(" ");

        if (startLineWords.length < 2) {
            return "";
        }

        return startLineWords[1].trim();
    }

    private static String parsePath(String url) {
        int index = url.indexOf("?");
        if (index == -1) {
            return url;
        }

        return url.substring(0, index).trim();
    }

    private static Map<String, String> parseParams(String url) {
        int index = url.indexOf("?");
        if (index == -1) {
            new HashMap<>();
        }

        return HttpRequestUtils.parseQueryString(url.substring(index + 1));
    }

    private static HttpRequestHeader parseHeader(String message) {
        String[] eachLine = message.split("\n");
        Map<HttpHeaders, String> map = new HashMap<>();

        for (String line : eachLine) {
            if ("".equals(line)) {
                break;
            }

            Pair headerKeyAndValue = HttpRequestUtils.parseHeader(line);
            if (headerKeyAndValue == null) {
                continue;
            }

            HttpHeaders header = HttpHeaders.match(headerKeyAndValue.getKey());
            if (!header.equals(HttpHeaders.NONE)) {
                map.put(header, headerKeyAndValue.getValue());
            }
        }

        return HttpRequestHeader.from(map);
    }

    private static HttpBody parseBody(String message) {
        List<String> eachLine = Arrays.stream(message.split("\n"))
                .collect(Collectors.toList());

        int crlfIndex = eachLine.indexOf(" ");
        if (crlfIndex == -1) {
            return HttpBody.of("");
        }

        List<String> body = eachLine.subList(crlfIndex, eachLine.size());
        return HttpBody.of(String.join("", body));
    }

    private static HttpBody parseBody(BufferedReader br, int contentLength) throws IOException {
        String body = IOUtils.readData(br, contentLength);

        return HttpBody.of(body);
    }
}
