package http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessions {
    private static Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getSession(String uuid) {
        return sessions.get(uuid);
    }

    public static HttpSession createSession() {
        String uuid = UUID.randomUUID().toString();
        sessions.put(uuid, new HttpSession(uuid));

        return sessions.get(uuid);
    }

    public static Map<String, HttpSession> getSessions() {
        return sessions;
    }
}
