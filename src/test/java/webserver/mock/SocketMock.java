package webserver.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SocketMock extends Socket {

    private final InputStream in;
    private final OutputStream out;

    public SocketMock(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public InetAddress getInetAddress() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isOutputShutdown() {
        return false;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return in;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return out;
    }
}
