package webserver.http;

public class HttpBody {

    private String body;

    private HttpBody(String body) {
        this.body = body;
    }

    public static HttpBody of(String body) {
        return new HttpBody(body);
    }

    public static HttpBody of(byte[] body) {
        return new HttpBody(new String(body));
    }

    public String getBody() {
        return body.trim();
    }

    public char[] toChar() {
        return body.toCharArray();
    }

    public byte[] toByte() {
        return body.getBytes();
    }
}
