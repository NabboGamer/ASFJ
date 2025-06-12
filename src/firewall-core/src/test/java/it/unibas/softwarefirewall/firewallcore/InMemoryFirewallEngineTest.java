package it.unibas.softwarefirewall.firewallcore;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallapi.EDirection;
import it.unibas.softwarefirewall.firewallapi.EProtocol;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.IRuleSet;
import it.unibas.softwarefirewall.firewallapi.ETypeOfOperation;
import java.util.List;
import java.util.concurrent.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InMemoryFirewallEngineTest {

    private Injector injector;
    private InMemoryFirewallEngine firewall;
    private TestPacketLogger testPacketLogger;

    @BeforeAll
    void initGuice() {
        // I use a module that overrides PacketLogger with a mock one cause in this
        // test i want to test only the behaviur of InMemoryFirewallEngine class
        injector = Guice.createInjector(new FirewallModule() {
            @Override
            protected void configure() {
                super.configure();
                // bind my spy logger instead of real PacketLogger
                bind(PacketLogger.class).to(TestPacketLogger.class).in(Singleton.class);
            }
        });
        firewall = injector.getInstance(InMemoryFirewallEngine.class);
        testPacketLogger = (TestPacketLogger) injector.getInstance(PacketLogger.class);
    }

    @Test
    @Order(1)
    void testSingleThreadBasicOperations() {
        IRuleSet rules = firewall.getActiveRuleSet();
        log.info("Current Active RuleSet: {}", rules.toString());
        assertNotNull(rules);

        // Create a rule that allow all inbound traffic
        IRule allowAll = new Rule("r1", "allow all", EDirection.INBOUND, 
                                  new IPRange("0.0.0.0/0"), new IPRange("192.168.0.0/24"),
                                  new PortRange(0, 65535), new PortRange(0, 65535),
                                  EProtocol.TCP);
        firewall.updateActiveRuleSet(allowAll, ETypeOfOperation.ADD);

        // Verify that the rule has been added
        List<IRule> activeRuleSet = firewall.getActiveRuleSet().getRules();
        assertTrue(activeRuleSet.contains(allowAll));

        // Create a package to localhost
        IPacket pkt = new PseudoPacket("1", new PseudoHeader("1.2.3.4", "192.168.0.140", 1000, 80, EProtocol.TCP),"");
        boolean result = firewall.processPacket(pkt);
        assertTrue(result);

        // Verify that the logger has received a log
        assertEquals(1, testPacketLogger.getEntries().size());
        assertTrue(testPacketLogger.getEntries().get(0).getAllowed());
    }

    @Test
    @Order(2)
    // TODO: Fix 
    void testConcurrentProcessing() throws InterruptedException {
        // If I remove the only rule in the RuleSet and there are none left, I am implicitly denying any packet
        firewall.updateActiveRuleSet(firewall.getActiveRuleSet().getRules().get(0), ETypeOfOperation.REMOVE);

        int threads = 2, iterations = 1;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        
        for (int i = 0; i < threads; i++) {
            exec.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    // I create a PseudoPacket with identical header but different port
                    IPacket pkt = new PseudoPacket("t" + j, new PseudoHeader("1.1.1.1","192.168.0.140", j, 80, EProtocol.TCP), "");
                    boolean ok = firewall.processPacket(pkt);
                    assertFalse(ok);  // It must always be false, because there are no rules
                }
                latch.countDown();  // Report that this thread has ended
            });
//            log.info("Thread {} have finished its operations", i);
        }

        
        assertTrue(latch.await(60, TimeUnit.SECONDS), "Threads did not finish in time");
        exec.shutdownNow();

        // Should have threads*iterations entries logged
        assertEquals(threads * iterations, testPacketLogger.getEntries().size());
    }

    // TestPacketLogger is a inner class that is useful for intercepting logs 
    // without depending on the real PacketLogger
    @Singleton
    @Getter
    public static class TestPacketLogger extends PacketLogger {
        private final List<PacketLogEntry> entries = new CopyOnWriteArrayList<>();
        @Override
        public void logPacket(IPacket packet, Boolean allowed) {
            entries.add(new PacketLogEntry(packet, allowed));
        }
    }
    
}
