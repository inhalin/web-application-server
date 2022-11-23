package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private Map<String, Object> datas = new HashMap<>();

    private String id;

    public HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        datas.put(name, value);
    }

    public Object getAttribute(String name) {
        return datas.get(name);
    }

    public void removeAttribute(String name) {
        datas.remove(name);
    }

    public void invalidate() {
        HttpSessions.remove(id);
    }
}
