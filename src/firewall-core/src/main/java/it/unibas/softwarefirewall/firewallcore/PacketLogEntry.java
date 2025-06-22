package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IPacketLogEntry;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PacketLogEntry implements IPacketLogEntry{
    private final IPacket packet;
    private final Boolean allowed;
    private final Long timestamp;

    public PacketLogEntry(IPacket packet, boolean allowed) {
        this.packet = packet;
        this.allowed = allowed;
        this.timestamp = System.currentTimeMillis();
    }
}

