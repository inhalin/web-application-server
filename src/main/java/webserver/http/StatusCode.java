package webserver.http;

public enum StatusCode {

    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found");

    private int code;
    private String text;

    StatusCode(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
