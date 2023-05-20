package dev.jmoore.net;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import dev.jmoore.Protocore;
import dev.jmoore.cache.Store;
import io.ttrms.skeleton.Handler;

public abstract class H<MinecraftPacketT extends Packet, StoreT extends Store> extends Handler<MinecraftPacketT, Protocore, Session, Packet, StoreT> {
    H(Protocore core) {
        super(core);
    }

    @Override
    protected abstract StoreT getStore();

    @SuppressWarnings("unchecked")
    public abstract static class Incoming<MinecraftPacketT extends Packet, StoreT extends Store> extends H<MinecraftPacketT, StoreT> {
        public Incoming(Protocore core) {
            super(core);
        }

        @Override
        public boolean handleIn(Session session, Packet packet) {
            return apply(session, (MinecraftPacketT) packet);
        }

        @Override
        public Packet handleOut(Session session, Packet packet) {
            throw new RuntimeException("Invalid operation for Incoming handler!");
        }

        @Override
        public void handlePostOut(Session session, Packet packet) {
            throw new RuntimeException("Invalid operation for Incoming handler!");
        }

        protected abstract boolean apply(Session session, MinecraftPacketT packet);
    }

    @SuppressWarnings("unchecked")
    public abstract static class Outgoing<MinecraftPacketT extends Packet, StoreT extends Store> extends H<MinecraftPacketT, StoreT> {
        public Outgoing(Protocore core) {
            super(core);
        }

        @Override
        public boolean handleIn(Session session, Packet packet) {
            throw new RuntimeException("Invalid operation for Outgoing handler!");
        }

        @Override
        public Packet handleOut(Session session, Packet packet) {
            return apply(session, (MinecraftPacketT) packet);
        }

        @Override
        public void handlePostOut(Session session, Packet packet) {
            throw new RuntimeException("Invalid operation for Outgoing handler!");
        }

        protected abstract Packet apply(Session session, MinecraftPacketT packet);
    }

    @SuppressWarnings("unchecked")
    public abstract static class PostOutgoing<MinecraftPacketT extends Packet, StoreT extends Store> extends H<MinecraftPacketT, StoreT> {
        public PostOutgoing(Protocore core) {
            super(core);
        }

        @Override
        public boolean handleIn(Session session, Packet packet) {
            throw new RuntimeException("Invalid operation for PostOutgoing handler!");
        }

        @Override
        public Packet handleOut(Session session, Packet packet) {
            throw new RuntimeException("Invalid operation for PostOutgoing handler!");
        }

        @Override
        public void handlePostOut(Session session, Packet packet) {
            apply(session, (MinecraftPacketT) packet);
        }

        protected abstract void apply(Session session, MinecraftPacketT packet);
    }
}
