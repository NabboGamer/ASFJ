package it.unibas.softwarefirewall.firewallcore;

import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class PacketLogger {

    private final Queue<PacketLogEntry> logQueue = new ConcurrentLinkedQueue<>();
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

    public void logPacket(IPacket packet, Boolean allowed) {
        logQueue.add(new PacketLogEntry(packet, allowed));
        this.cleanupOldEntries();
    }

    public Queue<PacketLogEntry> getSnapshot() {
        return new ConcurrentLinkedQueue<>(logQueue); // for GUI, secure copy
    }

    private void cleanupOldEntries() {
        Long now = System.currentTimeMillis();
        for (PacketLogEntry entry : logQueue) {
            if (now - entry.getTimestamp() > this.retentionInMillis) {
                logQueue.remove(entry);
            }
        }
    }
    
}

