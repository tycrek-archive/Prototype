package dev.jmoore;

package dev.ttrms.v2;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.codec.MinecraftCodec;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.tcp.TcpServer;
import dev.jmoore.log.Log;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.UUID;

@Getter
public class Server extends TcpServer {
    /**
     * The "message of the day" displayed in the clients' server list. <code>.setMotd()</code> Must be called before <code>.connect()</code>
     */
    @Setter private Component motd;
    private BufferedImage icon;

    public Server(Protocore core, String host, int port) {
        super(host, port, MinecraftProtocol.class);

        // Default global flags
        setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, true);
        setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 256);

        // Server login handler
        setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session -> {
            var store = core.getCache().getTarget();

            // If LoginStartHandler set the disconnect flag, disconnect the client
            if (session.getFlag("disconnect", false))
                session.send(new ServerDisconnectPacket(session.getFlag("disconnect-message").toString(), false));
            else session.send(new ServerJoinGamePacket(
                    getCore().getCache().getPlayer().getEntityId(),
                    store.isHardcore(),
                    store.getGamemode(),
                    store.getDimension(),
                    store.getDifficulty(),
                    store.getMaxPlayers(),
                    store.getWorldType(),
                    store.isReducedDebugInfo()));
        });

        // Server info builder. This information shows up in the multiplayer server list
        setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session -> {
            var coreStore = getCore().getCache().getCore();

            // Build the profile to display
            boolean isPlayerConnected = getCore().getPacketManager().isPrimarySessionReady();

            // Build a fancy component for the hover text
            var component = isPlayerConnected
                    ? Component.empty().append(Component.text("Player connected: ").color(NamedTextColor.GOLD)).append(Component.text(coreStore.getConnectedServerPlayer().getName()).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                    : Component.empty().append(Component.text("No player connected").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC));

            return new ServerStatusInfo(
                    new VersionInfo(MinecraftCodec.CODEC.getMinecraftVersion(), MinecraftCodec.CODEC.getProtocolVersion()),
                    new PlayerInfo(1, isPlayerConnected ? 1 : 0,
                            new GameProfile[]{new GameProfile(UUID.randomUUID(), LegacyComponentSerializer.legacySection().serialize(component))}),
                    Component.text("Hello world!"),
                    getIcon(),
                    false
            );
        });

        Log.SERVER.info("Server initialized");
    }

    /**
     * Alias for <code>bindImpl</code>
     */
    public void start(boolean wait, Runnable callback) {
        new Thread(() -> this.bind(wait, callback), "ServerThread").start();
    }

    /**
     * Sets the server from a URL
     *
     * @param url The URL to load an image from
     */
    // skipcq: JAVA-E1014
    public void setIcon(URL url) throws IOException {
        this.icon = ImageIO.read(url);
    }

    /**
     * Sets the server icon from a file
     *
     * @param file The image file to load
     */
    // skipcq: JAVA-E1014
    public void setIcon(File file) throws IOException {
        this.icon = ImageIO.read(file);
    }
}
