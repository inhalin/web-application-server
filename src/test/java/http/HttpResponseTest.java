package http;

import org.junit.Test;

import java.io.*;

public class HttpResponseTest {
    private String testDirectory = "./src/test/resources/output";

    @Test
    public void responseForward() throws Exception {
        HttpResponse response = new HttpResponse(createDataOutputStream("/Http_Forward.txt"));
        response.forward("/index.html");
    }

    @Test
    public void responseRedirect() throws Exception {
        HttpResponse response = new HttpResponse(createDataOutputStream("/Http_Redirect.txt"));
        response.sendRedirect("/index.html");
    }

    @Test
    public void responseCookies() throws Exception {
        HttpResponse response = new HttpResponse(createDataOutputStream("/Http_Cookie.txt"));
        response.addHeader("Set-Cookie", "logined=true");
        response.sendRedirect("/index.html");
    }

    private FileOutputStream createDataOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(new File(testDirectory + filename));
    }
}
