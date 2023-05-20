package dev.jmoore.net.listeners;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundDisconnectPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.*;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import dev.jmoore.ProtocolUtils;
import dev.jmoore.log.Log;
import dev.jmoore.net.PacketManager;
import lombok.*;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
// skipcq: JAVA-E0169
public class ServerListener implements com.github.steveice10.packetlib.event.server.ServerListener {
    private final PacketManager packetManager;
    private final AtomicReference<Session> session = new AtomicReference<>();
    @Getter
    private final Map<String, Session> extraSessions = new LinkedHashMap<>();
    @Getter
    @Setter
    private DisconnectedEvent lastDcEvent;

    @Override
    public void sessionAdded(SessionAddedEvent event) {
        val clientSession = event.getSession();

        // Is this the primary connection to the server?
        boolean isPrimarySession = session.get() == null;

        // Set this ServerListeners sessions
        if (isPrimarySession) session.set(clientSession);
        else extraSessions.put(clientSession.getRemoteAddress().toString(), clientSession);

        // Create a listener for this Session
        clientSession.addListener(new ServerSessionListener(packetManager, isPrimarySession));

        Log.SERVER.info("%s session added: %s", isPrimarySession ? "Primary" : "Extra", clientSession.getPort());
    }

    public Session getPrimarySession() {
        return session.get();
    }

    @Override
    public void sessionRemoved(SessionRemovedEvent event) {
        boolean isPrimarySession = !session.get().isConnected();

        // Remove sessions
        if (isPrimarySession) {
            session.set(null);

            if (!ProtocolUtils.Sub.isStatus(event.getSession()) && !event.getSession().getFlag("disconnect", false)) {
                // Reset core cache
                packetManager.getCore().getMonitorDevice().resetNotifications();
                packetManager.getCore().getCache().getCore().setAfkTime(packetManager.getCore().getTimeUtils().getSystemSeconds());
                packetManager.getCore().getCache().getCore().setConnectedServerPlayer(null);
                packetManager.getCore().getCache().getCore().setPlayerHadConnected(false);

                // Set server info to defaults
                packetManager.getCore().getServer().resetInfo();
            }
        } else extraSessions.entrySet().stream()
                .filter(((stringSessionEntry) -> !stringSessionEntry.getValue().isConnected()))
                .forEach((session) -> extraSessions.remove(session.getKey()));

        Log.SERVER.info("%s session removed", isPrimarySession ? "Primary" : "Extra");
    }

    @Override
    public void serverBound(ServerBoundEvent event) {
        Log.SERVER.debug("Server bound to port %s:%d", event.getServer().getHost(), event.getServer().getPort());
    }

    @Override
    @SneakyThrows
    public void serverClosing(ServerClosingEvent event) {
        Log.SERVER.debug("Server closing...");
        var reason = Component.text("Server closing");

        // Disconnect primary session
        if (session.get() != null) session.get().send(new ClientboundDisconnectPacket(reason));

        // Disconnect all extra sessions too
        extraSessions.forEach((address, session) -> session.send(new ClientboundDisconnectPacket(reason)));
    }

    @Override
    public void serverClosed(ServerClosedEvent event) {
        // Remove all Sessions
        session.set(null);
        extraSessions.clear();
        Log.SERVER.info("Server closed");
    }
}
