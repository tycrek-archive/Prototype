package dev.jmoore.net.listeners;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import dev.jmoore.ProtocolUtils;
import dev.jmoore.log.Log;
import dev.jmoore.net.PacketManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientListener implements SessionListener {
    private final PacketManager packetManager;

    @Override
    public void packetReceived(Session session, Packet packet) {
        //System.out.printf("[Client] Packet received (%s): %s%n", ProtocolUtils.Sub.toSubProtocol(session), packet.getClass().getSimpleName());
        if (ProtocolUtils.Sub.isGame(session))
            packetManager.clientIncomingHandler(session, packet);
    }

    @Override
    public void packetSending(PacketSendingEvent event) {
        packetManager.clientOutgoingHandler(event);
    }

    @Override
    public void packetSent(Session session, Packet packet) {
        packetManager.clientPostOutgoingHandler(session, packet);
    }

    @Override
    public void packetError(PacketErrorEvent event) {
        Log.CLIENT.error("Packet error: %s", event.getCause());
    }

    @Override
    public void connected(ConnectedEvent event) {
        Log.CLIENT.info("Connected to %s", event.getSession().getRemoteAddress().toString());
    }

    @Override
    public void disconnecting(DisconnectingEvent event) {
        Log.CLIENT.debug("Disconnecting...");
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        if (event.getCause() != null) event.getCause().printStackTrace();
        Log.CLIENT.info("Disconnected: %s", event.getReason());
    }
}
