package it.unibas.softwarefirewall.firewallapi;

import java.util.Queue;

public interface IPacketLogger {
    void addPacketLoggerListener(IPacketLoggerListener listener);
    void logPacket(IPacket packet, Boolean allowed);
    Queue<IPacketLogEntry> getSnapshot();
    void cleanupOldEntries();
}