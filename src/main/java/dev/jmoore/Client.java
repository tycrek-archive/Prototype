package dev.jmoore;

import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import dev.jmoore.log.Log;

public class Client extends TcpClientSession {

    private final AuthenticationService auth;

    public Client(String host, int port, AuthenticationService auth) {
        super(host, port, new MinecraftProtocol(auth.getSelectedProfile(), auth.getAccessToken()));
        this.auth = auth;
    }

    public void connect(Runnable afterConnect) {
        connect();
        afterConnect.run();
    }

    @Override
    public void connect() {
        doConnect(true);
    }

    @Override
    public void connect(boolean wait) {
        doConnect(wait);
    }

    private void doConnect(boolean wait) {
        Log.CLIENT.info("Doing connection attempt...");
        if (isConnected())
            throw new RuntimeException("Already connected!");
        else if (!auth.isLoggedIn())
            throw new RuntimeException(String.format("Account \"%s\" is not signed in!", auth.getUsername()));
        else new Thread(() -> super.connect(wait), "ClientThread").start();
    }
}
