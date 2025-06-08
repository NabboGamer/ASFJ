package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IPacket;
import lombok.Getter;

@Getter
public class PacketLogEntry {
    private final IPacket packet;
    private final Boolean allowed;
    private final Long timestamp;

    public PacketLogEntry(IPacket packet, boolean allowed) {
        this.packet = packet;
        this.allowed = allowed;
        this.timestamp = System.currentTimeMillis();
    }
}

