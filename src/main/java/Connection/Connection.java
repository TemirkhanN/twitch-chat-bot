package Connection;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connection {
    private Socket instance;

    public BufferedReader dataReceiver;

    private Writer dataSender;

    private enum State {
        ACTIVE,
        INACTIVE
    }

    private State status;

    public Connection() throws IOException {
        instance = new Socket("irc.chat.twitch.tv", 6667);

        Charset charset = Charset.forName("utf-8");
        dataReceiver = new BufferedReader(new InputStreamReader(instance.getInputStream(), charset));
        dataSender   = new BufferedWriter(new OutputStreamWriter(instance.getOutputStream(), charset));
        status = State.ACTIVE;
    }

    public void send(Request[] requests) throws IOException {
        for (Request request : requests) {
            dataSender.write(request.toString());
        }
        dataSender.flush();
    }

    public void send(Request request) throws IOException {
        dataSender.write(request.toString());
        dataSender.flush();
    }

    public void close() {
        if (instance.isClosed()) {
            status = State.INACTIVE;

            return;
        }

        try {
            instance.close();
        } catch (IOException e) {
            // handleBy
        } finally {
            status = State.INACTIVE;
        }
    }


    public boolean isActive() {
        return status == State.ACTIVE;
    }
}
