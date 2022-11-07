package http;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(testDirectory + "Http_GET.txt")));
        HttpRequest request = new HttpRequest(br);

        Assert.assertEquals(HttpMethod.GET, request.getMethod());
        Assert.assertEquals("/user/create", request.getPath());
        Assert.assertEquals("keep-alive", request.getHeader("Connection"));
        Assert.assertEquals("javajigi", request.getParameter("userId"));
    }
}
