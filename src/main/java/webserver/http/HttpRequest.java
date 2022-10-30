package webserver.http;

import java.util.Map;

public class HttpRequest {

    private HttpRequestHeader header;
    private HttpBody body;
    private Method method;
    private String url;
    private String path;
    private Map<String, String> params;

    private HttpRequest(
            HttpRequestHeader header,
            HttpBody body,
            Method method,
            String url,
            String path,
            Map<String, String> params)
    {
        this.header = header;
        this.body = body;
        this.method = method;
        this.url = url;
        this.path = path;
        this.params = params;
    }

    public static HttpRequest of(
            HttpRequestHeader header,
            HttpBody body,
            Method method,
            String url,
            String path,
            Map<String, String> params)
    {
        return new HttpRequest(header, body, method, url, path, params);
    }

    public HttpRequestHeader getHeader() {
        return header;
    }

    public HttpBody getBody() {
        return body;
    }

    public Method getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
