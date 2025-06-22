package it.unibas.softwarefirewall.firewallcore;

import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IPacketLogEntry;
import it.unibas.softwarefirewall.firewallapi.IPacketLogger;
import it.unibas.softwarefirewall.firewallapi.IPacketLoggerListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class PacketLogger implements IPacketLogger {

    private final Queue<IPacketLogEntry> logQueue = new ConcurrentLinkedQueue<>();
    private final List<IPacketLoggerListener> listeners = new CopyOnWriteArrayList<>();
    private final Long retentionInMillis;

    public PacketLogger() {
        Properties props = new Properties();
        Long retention;
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("firewall-core.properties")) {
            props.load(in);
            String retentionProperty = props.getProperty("firewallcore.packetlogger.retention");
            retention = retentionProperty != null ? Long.valueOf(retentionProperty) : 300_000L;
        } catch (IOException | NumberFormatException e) {
            log.error("Could not load PacketLogger configuration: {}", e);
            retention = Long.valueOf(300000);
        }
        this.retentionInMillis = retention;
    }
    
    public void addPacketLoggerListener(IPacketLoggerListener listener) {
        this.listeners.add(listener);
    }

    public void logPacket(IPacket packet, Boolean allowed) {
        IPacketLogEntry newPacketLogEntry = new PacketLogEntry(packet, allowed);
        logQueue.add(newPacketLogEntry);
        listeners.forEach(IPacketLoggerListener::onNewEntry);
        this.cleanupOldEntries();
    }

    public Queue<IPacketLogEntry> getSnapshot() {
        return new ConcurrentLinkedQueue<>(logQueue); // for GUI, secure copy
    }

    public void cleanupOldEntries() {
        Long now = System.currentTimeMillis();
        for (IPacketLogEntry entry : logQueue) {
            if (now - entry.getTimestamp() > this.retentionInMillis) {
                logQueue.remove(entry);
            }
        }
    }
    
}

