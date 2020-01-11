package club.tulane.tomcat;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class NioChannel {

    protected SocketChannel sc = null;
    protected NioEndpoint.Poller poller;

    public void setPoller(NioEndpoint.Poller poller) {
        this.poller = poller;
    }

    public void setIOChannel(SocketChannel IOChannel) {
        this.sc = IOChannel;
    }


    public NioChannel(SocketChannel sc) {
        this.sc = sc;
    }

    public NioEndpoint.Poller getPoller() {
        return poller;
    }

    public SocketChannel getIOChannel() {
        return sc;
    }

    public void close(boolean force) throws IOException {
        if(isOpen() || force) close();
    }

    private void close() throws IOException {
        getIOChannel().socket().close();
        getIOChannel().close();
    }

    private boolean isOpen() {
        return sc.isOpen();
    }
}
