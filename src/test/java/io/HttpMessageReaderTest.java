package io;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class HttpMessageReaderTest {
    @Test
    public void UrlPath를_읽을_수_있다() throws IOException {
        // given
        final String given = "/favicon.ico";
        InputStream in = new ByteArrayInputStream("GET /favicon.ico HTTP/1.1".getBytes());
        HttpMessageReader reader = new HttpMessageReader(in);

        // when
        final String actual = reader.readUrlPath();

        // then
        assertThat(given, is(actual));
    }
}