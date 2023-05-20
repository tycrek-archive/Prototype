package dev.jmoore;

import dev.jmoore.cache.Cache;
import dev.jmoore.log.Log;
import dev.jmoore.net.PacketManager;
import dev.jmoore.net.listeners.ClientListener;
import dev.jmoore.net.listeners.ServerListener;
import io.ttrms.skeleton.AbstractCore;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class Protocore extends AbstractCore<Void/*NeoConfig*/, Cache, Client, Server, Void, PacketManager, Void/*DiscordBot*/, Account, Void/*Crypto*/> {
    private Account account;
    private Client client;

    public void init(Consumer<String> deviceCodeConsumer) throws Exception {
        Log.CORE.info("Initializing Protocore");

        this.account = new Account();
        this.account.setDeviceCodeConsumer(deviceCodeConsumer);
        this.account.login();

        this.client = new Client("10.0.0.202", 25565, this.account.getAuth());
    }

    private void setupPacketManager() {
        packetManager = new PacketManager(this);
        packetManager.addListeners(new ServerListener(packetManager), new ClientListener(packetManager));
    }
}
