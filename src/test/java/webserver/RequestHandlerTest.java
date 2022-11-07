package webserver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import webserver.mock.SocketMock;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

class RequestHandlerTest {

    @MethodSource
    @Test
    public void static_file_test(String given, String expected) throws IOException, InterruptedException {
        // given
        InputStream in = new ByteArrayInputStream(given.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        Socket socket = new SocketMock(in, byteArrayOutputStream);
        RequestHandler reqHandler = new RequestHandler(socket);

        // when
        reqHandler.run();
        reqHandler.join(1000);


        // then
        String actual = byteArrayOutputStream.toString();
        assertThat(actual, is(expected));
    }

    private Stream<Arguments> static_file_test() throws IOException {
        return Stream.of(
                of("GET /css/styles.css HTTP/1.1", staticFileExpected())
        );
    }

    private String staticFileExpected() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream(1024);
        DataOutputStream dos = new DataOutputStream(b);
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Length: 7065 \r\n");
        dos.writeBytes("Content-Type: text/css; \r\n");
        dos.writeBytes("\r\n");

        byte[] file = Files.readAllBytes(new File(System.getProperty("user.dir") + "/webapp/css/styles.css").toPath());
        dos.write(file, 0, file.length);
        return b.toString();
    }

}