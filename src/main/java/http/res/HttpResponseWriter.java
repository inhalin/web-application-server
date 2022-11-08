package http.res;

import http.protocol.HttpResponse;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponseWriter {

    private final DataOutputStream out;
    private final HttpResponse response;

    public HttpResponseWriter(OutputStream out, HttpResponse response) {
        this.out = new DataOutputStream(out);
        this.response = response;
    }

    public void write() throws IOException {
        writeFirstLine();
        writeHeader();
        writeBody();
        flush();
    }

    private void writeFirstLine() throws IOException {
        out.writeBytes(response.getFirstLine());
    }

    private void writeHeader() throws IOException {
        out.writeBytes(response.getHeaderLines());
    }

    private void writeBody() throws IOException {
        byte[] b = response.getBody();
        out.write(b, 0, b.length);
    }

    private void flush() throws IOException {
        out.flush();
    }
}
