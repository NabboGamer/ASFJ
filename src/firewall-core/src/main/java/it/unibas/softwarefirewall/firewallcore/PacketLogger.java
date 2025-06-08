package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IPacket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Iterator;

public class PacketLogger {

    private final Queue<PacketLogEntry> logQueue = new ConcurrentLinkedQueue<>();
    private final Long retentionMillis;

    public PacketLogger(Long retentionMillis) {
        this.retentionMillis = retentionMillis;
    }

    public void logPacket(IPacket packet, Boolean allowed) {
        logQueue.add(new PacketLogEntry(packet, allowed));
        cleanupOldEntries();
    }

    public Queue<PacketLogEntry> getSnapshot() {
        return new ConcurrentLinkedQueue<>(logQueue); // per la GUI, copia sicura
    }

    private void cleanupOldEntries() {
        Long now = System.currentTimeMillis();
        Iterator<PacketLogEntry> iterator = logQueue.iterator();
        while (iterator.hasNext()) {
            PacketLogEntry entry = iterator.next();
            if (now - entry.getTimestamp() > retentionMillis) {
                iterator.remove();
            } else {
                break; // coda in ordine d'inserimento -> possiamo fermarci
            }
        }
    }
    
}

