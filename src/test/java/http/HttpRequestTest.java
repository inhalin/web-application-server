package http;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/input";

    @Test
    public void request_GET() throws Exception {
        HttpRequest request = new HttpRequest(readFile("/Http_GET.txt"));

        Assert.assertEquals(HttpMethod.GET, request.getMethod());
        Assert.assertEquals("/user/create", request.getPath());
        Assert.assertEquals("keep-alive", request.getHeader("Connection"));
        Assert.assertEquals("javajigi", request.getParameter("userId"));
    }

    @Test
    public void request_POST() throws Exception {
        HttpRequest request = new HttpRequest(readFile("/Http_POST.txt"));

        Assert.assertEquals(HttpMethod.POST, request.getMethod());
        Assert.assertEquals("/user/create", request.getPath());
        Assert.assertEquals("keep-alive", request.getHeader("Connection"));
        Assert.assertEquals("javajigi", request.getParameter("userId"));
    }

    private FileReader readFile(String filename) throws FileNotFoundException {
        return new FileReader(new File(testDirectory + filename));
    }
}
