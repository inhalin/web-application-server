package http;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript");

    private String ext;
    private String contentType;

    ContentType(String ext, String contentType) {
        this.ext = ext;
        this.contentType = contentType;
    }

    public String getExt() {
        return ext;
    }

    public String getContentType() {
        return contentType;
    }

    public static String findByExt(String ext) {
        for (ContentType type : ContentType.values()) {
            if (type.getExt().equals(ext)) {
                return type.getContentType();
            }
        }

        return null;
    }
}
