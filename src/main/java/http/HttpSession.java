package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private Map<String, Object> datas = new HashMap<>();

    public HttpSession(String id) {
        datas.put("JSESSIONID", id);
    }

    public String getId() {
        return datas.get("JSESSIONID").toString();
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
        for (String key : datas.keySet()) {
            if (!"JSESSIONID".equals(key)) {
                datas.remove(key);
            }
        }
    }
}
