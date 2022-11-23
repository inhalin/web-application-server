package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    public static final String SESSION_NAME = "JSESSIONID";
    private static Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getSession(String id) {
        HttpSession session = sessions.get(id);
        if (session == null) {
            HttpSession newSession = new HttpSession(id);
            sessions.put(id, newSession);
            return newSession;
        }
        return session;
    }

    public static void remove(String id) {
        sessions.remove(id);
    }
}
