package dev.jmoore.net.listeners;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import dev.jmoore.ProtocolUtils;
import dev.jmoore.log.Log;
import dev.jmoore.net.PacketManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerSessionListener implements SessionListener {

    private final PacketManager packetManager;
    private final boolean isPrimary;

    @Override
    public void packetReceived(Session session, Packet packet) {
        Log.SESSION.debug("Packet received (%s): %s",
                ((MinecraftProtocol) session.getPacketProtocol()).getState(),
                packet.getClass().getSimpleName());
        if (ProtocolUtils.Sub.isLogin(session))
            packetManager.serverLoginHandler(session, packet);
        if (ProtocolUtils.Sub.isGame(session))
            packetManager.serverIncomingHandler(session, packet, isPrimary);
    }

    @Override
    public void packetSending(PacketSendingEvent event) {
        if (!event.getPacket().getClass().getSimpleName().contains("Entity"))
            Log.SESSION.debug("Packet sending (%s): %s",
                    ((MinecraftProtocol) event.getSession().getPacketProtocol()).getState(),
                    event.getPacket().getClass().getSimpleName());
        packetManager.serverOutgoingHandler(event);
    }

    @Override
    public void packetSent(Session session, Packet packet) {
        if (ProtocolUtils.Sub.isGame(session))
            packetManager.serverPostOutgoingHandler(session, packet);
    }

    @Override
    public void packetError(PacketErrorEvent event) {
        event.getCause().printStackTrace();
        Log.SESSION.error("Packet error (%s): %s",
                ((MinecraftProtocol) event.getSession().getPacketProtocol()).getState(),
                event.getCause());
    }

    @Override
    public void connected(ConnectedEvent event) {
        Log.SESSION.info("Connection from %s", event.getSession().getRemoteAddress().toString());
    }

    @Override
    public void disconnecting(DisconnectingEvent event) {
        Log.SESSION.debug("Disconnecting...");
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        Log.SESSION.info("Disconnected: %s", event.getReason());
        packetManager.getServerListener().setLastDcEvent(event);
    }
}
