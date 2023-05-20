package dev.jmoore;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.ProtocolState;
import com.github.steveice10.packetlib.Session;

public class ProtocolUtils {
    /**
     * Pseudo-class for ProtocolState utilities
     */
    public static class Sub {
        public static ProtocolState toSubProtocol(Session session) {
            return ((MinecraftProtocol) session.getPacketProtocol()).getState();
        }

        public static boolean isLogin(Session session) {
            return ((MinecraftProtocol) session.getPacketProtocol()).getState() == ProtocolState.LOGIN;
        }

        public static boolean isGame(Session session) {
            return ((MinecraftProtocol) session.getPacketProtocol()).getState() == ProtocolState.GAME;
        }

        public static boolean isHandshake(Session session) {
            return ((MinecraftProtocol) session.getPacketProtocol()).getState() == ProtocolState.HANDSHAKE;
        }

        public static boolean isStatus(Session session) {
            return ((MinecraftProtocol) session.getPacketProtocol()).getState() == ProtocolState.STATUS;
        }
    }
}
