package webserver;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static Map<String, Controller> handler = new HashMap<>();

    static {
        handler.put("/user/create", new CreateUserController());
        handler.put("/user/login", new LoginController());
        handler.put("/user/list", new ListUserController());
    }

    public static Controller getController(String path) {
        return handler.getOrDefault(path, null);
    }
}
