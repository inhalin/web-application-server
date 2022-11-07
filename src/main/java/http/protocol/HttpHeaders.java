package http.protocol;

import java.util.Map;

public class HttpHeaders {

    private final Map<HttpHeader, String> headers;

    public HttpHeaders(Map<HttpHeader, String> headers) {
        this.headers = headers;
    }

    public Map<HttpHeader, String> getHeaders() {
        return headers;
    }

    public String getCookie() {
        return headers.get(HttpHeader.COOKIES);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<HttpHeader, String> entry : headers.entrySet()) {
            String line = String.format("%s: %s \r\n", entry.getKey().getValue(), entry.getValue());
            sb.append(line);
        }
        sb.append("\r\n");
        return sb.toString();
    }
}
