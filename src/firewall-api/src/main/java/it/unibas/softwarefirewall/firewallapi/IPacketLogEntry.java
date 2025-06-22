package it.unibas.softwarefirewall.firewallapi;

public interface IPacketLogEntry {
    IPacket getPacket();
    Boolean getAllowed();
    Long getTimestamp();
}