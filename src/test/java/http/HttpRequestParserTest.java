package http;

import http.protocol.HttpMethod;
import http.protocol.HttpRequest;
import http.protocol.HttpVersion;
import http.req.HttpRequestParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class HttpRequestParserTest {

    @ParameterizedTest
    @MethodSource
    public void first_line_parse_test(String given, HttpMethod expectedMethod, String expectedUrl, HttpVersion expectedVersion) throws IOException {
        // given
        InputStream in = new ByteArrayInputStream(given.getBytes(StandardCharsets.UTF_8));

        // when
        HttpRequest req = new HttpRequestParser(in).parse();

        // then
        assertThat(req.getMethod(), is(expectedMethod));
        assertThat(req.getUrl(), is(expectedUrl));
        assertThat(req.getVersion(), is(expectedVersion));
    }

    private static Stream<Arguments> first_line_parse_test() {
        return Stream.of(
                arguments("GET /path HTTP/1.1", HttpMethod.GET, "/path", HttpVersion.HTTP11),
                arguments("POST /deep/path HTTP/1.1", HttpMethod.POST, "/deep/path", HttpVersion.HTTP11),
                arguments("NONE /super/deep/path NONE", HttpMethod.NONE, "/super/deep/path", HttpVersion.NONE),
                arguments("NONE /index.html NONE", HttpMethod.NONE, "/index.html", HttpVersion.NONE)
        );
    }
}
