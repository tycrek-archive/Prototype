package dev.jmoore.net;

import com.github.steveice10.packetlib.packet.Packet;
import io.ttrms.skeleton.IPacketRegister;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor
public class PacketRegister<HandlerT extends H<? extends Packet, ?>>
        implements IPacketRegister<PacketRegister<HandlerT>, HandlerT, Packet> {

    private final HashMap<Class<? extends Packet>, HandlerT> handlerMap = new HashMap<>();

    @Override
    public PacketRegister<HandlerT> add(Class<? extends Packet> packetClass, HandlerT handler) {
        this.handlerMap.put(packetClass, handler);
        return this;
    }

    @Override
    public HandlerT getHandler(Packet packet) {
        return this.handlerMap.get(packet.getClass());
    }
}
