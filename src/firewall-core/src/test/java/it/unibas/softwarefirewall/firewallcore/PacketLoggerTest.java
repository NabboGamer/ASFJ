package it.unibas.softwarefirewall.firewallcore;

import com.google.inject.Guice;
import com.google.inject.Injector;
import it.unibas.softwarefirewall.firewallapi.EProtocol;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PacketLoggerTest {
    
    private Injector injector;
    private InMemoryFirewallEngine firewall;
    private PacketLogger packetLogger;
    
    @BeforeAll
    public void setUp() {
        this.injector = Guice.createInjector(new FirewallModule());
        this.firewall = injector.getInstance(InMemoryFirewallEngine.class);
        this.packetLogger = injector.getInstance(PacketLogger.class);
    }
    
    @Test
    public void testPacketLogger() {
        // Create a packet to send to localhost
        log.info(firewall.getActiveRuleSetRules().toString());
        IPacket pkt1 = new PseudoPacket(new PseudoHeader("1.2.3.4", "192.168.0.140", 1000, 80, EProtocol.TCP),"");
        IPacket pkt2 = new PseudoPacket(new PseudoHeader("4.3.2.1", "192.168.0.140", 400, 1000, EProtocol.TCP),"");
        boolean result1 = firewall.activeRuleSetProcessPacket(pkt1);
        boolean result2 = firewall.activeRuleSetProcessPacket(pkt2);
        assertTrue(result1);
        assertFalse(result2);
        // Verify that the logger has received a log
        assertEquals(2, this.packetLogger.getSnapshot().size());
        assertTrue(this.packetLogger.getSnapshot().peek().getAllowed());
        log.info("PacketLogEntry registered from the PacketLogger: ");
        this.packetLogger.getSnapshot().stream().forEach(packetLogEntry -> log.info(packetLogEntry.toString()));
    }

}
