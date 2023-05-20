package dev.jmoore.net;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketSendingEvent;
import com.github.steveice10.packetlib.packet.Packet;
import dev.jmoore.Protocore;
import dev.jmoore.cache.Store;
import dev.jmoore.log.Log;
import dev.jmoore.net.listeners.ClientListener;
import dev.jmoore.net.listeners.ServerListener;
import io.ttrms.skeleton.AbstractPacketManager;
import lombok.Getter;

import java.util.Map;

@Getter
public class PacketManager extends AbstractPacketManager<Protocore, ServerListener, ClientListener> {

    private final PacketRegister<H.Incoming<? extends Packet, ? extends Store>> serverLoginRegister = new PacketRegister<>();
    private final PacketRegister<H.Incoming<? extends Packet, ? extends Store>> serverIncomingRegister = new PacketRegister<>();
    private final PacketRegister<H.Outgoing<? extends Packet, ? extends Store>> serverOutgoingRegister = new PacketRegister<>();
    private final PacketRegister<H.PostOutgoing<? extends Packet, ? extends Store>> serverPostOutgoingRegister = new PacketRegister<>();
    private final PacketRegister<H.Incoming<? extends Packet, ? extends Store>> clientIncomingRegister = new PacketRegister<>();
    private final PacketRegister<H.Outgoing<? extends Packet, ? extends Store>> clientOutgoingRegister = new PacketRegister<>();
    private final PacketRegister<H.PostOutgoing<? extends Packet, ? extends Store>> clientPostOutgoingRegister = new PacketRegister<>();

    /**
     * The PacketManager class manages all communications between the Servers and Clients
     */
    public PacketManager(Protocore core) {
        super(core);

        // Build packet registers (divided for collapsable sections in IDE)
        buildSLR();
        buildSIR();
        buildSOR();
        buildSPOR();
        buildCIR();
        buildCOR();
        buildCPOR();

        Log.PACKMAN.info("PacketManager initialized");
    }

    //region Listener operations
    public ServerListener getServerListener() {
        return serverListener;
    }

    public ClientListener getClientListener() {
        return clientListener;
    }

    public void addListeners(ServerListener serverListener, ClientListener clientListener) {
        this.serverListener = serverListener;
        this.clientListener = clientListener;

        if (this.core.getServer() != null) this.core.getServer().addListener(serverListener);
        if (this.core.getClient() != null) this.core.getClient().addListener(clientListener);

        Log.PACKMAN.info("Added listeners");
    }

    public void removeServerListener() {
        this.core.getServer().removeListener(serverListener);
        this.serverListener = null;
        Log.PACKMAN.info("Removed ServerListener");
    }

    public void removeClientListener() {
        this.core.getClient().removeListener(clientListener);
        this.clientListener = null;
        Log.PACKMAN.info("Removed ClientListener");
    }
    //endregion

    //region Build packet registers
    private void buildSLR() {
    }

    private void buildSIR() {
    }

    private void buildSOR() {
    }

    private void buildSPOR() {
    }

    private void buildCIR() {
    }

    private void buildCOR() {
    }

    private void buildCPOR() {
        // Empty until implemented
    }
    //endregion

    //region Packet handler functions

    /**
     * Incoming server session handler for LOGIN SubProtocol packets
     */
    public void serverLoginHandler(Session session, Packet packet) {
        try {
            var handler = serverLoginRegister.getHandler(packet);
            if (handler != null) handler.handleIn(session, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void serverIncomingHandler(Session session, Packet packet, boolean isPrimary) {
        if (isPrimary)
            try {
                var handler = serverIncomingRegister.getHandler(packet);
                if (handler == null || handler.handleIn(session, packet))
                    core.sendClientPacket(packet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    public void serverOutgoingHandler(PacketSendingEvent event) {
        try {
            var handler = serverOutgoingRegister.getHandler(event.getPacket());
            if (handler != null) {
                var packet = handler.handleOut(event.getSession(), event.getPacket());
                if (packet != null) event.setPacket(packet);
                else event.setCancelled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void serverPostOutgoingHandler(Session session, Packet packet) {
        try {
            var handler = serverPostOutgoingRegister.getHandler(packet);
            if (handler != null) handler.handlePostOut(session, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Called by a {@link ClientListener} when a packet is received from the remote server.
     * If possible, it forwards the packet to a connected player.
     */
    public void clientIncomingHandler(Session session, Packet packet) {
        try {
            var handler = clientIncomingRegister.getHandler(packet);
            if (handler == null || handler.handleIn(session, packet)) {
                if (isPrimarySessionReady()) serverListener.getPrimarySession().send(packet);
                serverListener.getExtraSessions().forEach((address, extraSession) -> {
                    if (extraSession.isConnected() && ProtocolUtils.Sub.isGame(extraSession))
                        extraSession.send(packet);
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void clientOutgoingHandler(PacketSendingEvent event) {
        try {
            var handler = clientOutgoingRegister.getHandler(event.getPacket());
            if (handler != null) {
                var packet = handler.handleOut(event.getSession(), event.getPacket());
                if (packet != null) event.setPacket(packet);
                else event.setCancelled(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Called by a {@link ClientListener} after a packet has been sent to the target server.
     */
    public void clientPostOutgoingHandler(Session session, Packet packet) {
        try {
            var handler = clientPostOutgoingRegister.getHandler(packet);
            if (handler != null) handler.handlePostOut(session, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //endregion
}
