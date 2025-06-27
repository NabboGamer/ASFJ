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
import java.util.Optional;
import java.util.concurrent.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InMemoryFirewallEngineTest {

    private InMemoryFirewallEngine firewall;
    private TestPacketLogger testPacketLogger;

    @BeforeAll
    public void setUp() {
        // I use a module that overrides PacketLogger with a mock one cause in this
        // test i want to test only the behavior of InMemoryFirewallEngine class
        // bind my test logger instead of real PacketLogger
        Injector injector = Guice.createInjector(new FirewallCoreModule() {
            @Override
            protected void configure() {
                super.configure();
                // bind my test logger instead of real PacketLogger
                bind(PacketLogger.class).to(TestPacketLogger.class).in(Singleton.class);
            }
        });
        firewall = injector.getInstance(InMemoryFirewallEngine.class);
        testPacketLogger = (TestPacketLogger) injector.getInstance(PacketLogger.class);
    }
    
    @BeforeEach
    public void cleanUp() {
        firewall.getActiveRuleSet().getRules().clear();
        testPacketLogger.getEntries().clear();
    }

    @Test
    @Order(1)
    public void testSingleThreadBasicOperations() {
        //// 1. Test of the getActiveRuleSet method and the injection mechanism of Guice
        IRuleSet rules = firewall.getActiveRuleSet();
        log.info("Current Active RuleSet: {}", rules.toString());
        assertNotNull(rules);
        
        
        //// 2. Test of the updateActiveRuleSet method
        // Create a rule that allow all inbound traffic
        IRule allowAll = new Rule("allow all", EDirection.INBOUND,
                                  new IPRange("0.0.0.0/0"), new IPRange("192.168.0.0/24"),
                                  new PortRange(0, 65535), new PortRange(0, 65535),
                                  EProtocol.TCP);
        firewall.updateActiveRuleSet(allowAll, ETypeOfOperation.ADD, Optional.empty());
        // Verify that the rule has been added
        List<IRule> activeRuleSet = firewall.getActiveRuleSet().getRules();
        assertTrue(activeRuleSet.contains(allowAll));
        
        //Create a updated versione of the allowAll rule created previously
        IRule updatedRule = new Rule("allow all updated", EDirection.OUTBOUND,
                                    new IPRange("0.0.0.0/0"), new IPRange("192.168.0.0/24"),
                                    new PortRange(0, 65535), new PortRange(0, 65535),
                                    EProtocol.TCP);
        log.info("RuleSet pre-update: {}", firewall.getActiveRuleSet().getRules());
        firewall.updateActiveRuleSet(allowAll, ETypeOfOperation.UPDATE, Optional.of(updatedRule));
        // Verify that the rule has been updated
        log.info("RuleSet post-update: {}", firewall.getActiveRuleSet().getRules());
        assertEquals(EDirection.OUTBOUND, firewall.getActiveRuleSet().getRules().getFirst().getDirection());

        // Add one different rules to the RuleSet
        IRule allowAllSecure = new Rule("allow all secure", EDirection.INBOUND,
                                  new IPRange("0.0.0.0/0"), new IPRange("192.168.0.0/24"),
                                  new PortRange(1000, 1000), new PortRange(80, 80),
                                  EProtocol.TCP);
        firewall.updateActiveRuleSet(allowAllSecure, ETypeOfOperation.ADD, Optional.empty());
        log.info("RuleSet post-add: {}", firewall.getActiveRuleSet().getRules());
        // Remove previously added rule
        firewall.updateActiveRuleSet(allowAll, ETypeOfOperation.REMOVE, Optional.empty());
        log.info("RuleSet post-remove: {}", firewall.getActiveRuleSet().getRules());
        assertEquals(1, firewall.getActiveRuleSet().getRules().size());
        assertEquals("allow all secure", firewall.getActiveRuleSet().getRules().getFirst().getDescription());
        
        
        //// 3. Test of the processPacket method
        // Create a packet to send to localhost
        IPacket pkt = new PseudoPacket(new PseudoHeader("1.2.3.4", "192.168.0.140", 1000, 80, EProtocol.TCP),"");
        boolean result = firewall.activeRuleSetProcessPacket(pkt);
        assertTrue(result);
        // Verify that the logger has received a log
        assertEquals(1, testPacketLogger.getEntries().size());
        assertTrue(testPacketLogger.getEntries().getFirst().getAllowed());
    }

    @Test
    @Order(2)
    public void testConcurrentProcessing() throws InterruptedException {
        // If I dont add any rule all the packets will be denied
        log.info("The Active RuleSet actually have {} rules", firewall.getActiveRuleSet().getRules().size());

        int threads = 20, iterations = 100;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        
        for (int i = 0; i < threads; i++) {
            exec.submit(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        IPacket pkt = new PseudoPacket(new PseudoHeader("1.1.1.1", "192.168.0.140", j, 80, EProtocol.TCP), "");
                        boolean ok = firewall.activeRuleSetProcessPacket(pkt);
                        assertFalse(ok, "Expected packet to be denied due to empty rule set");
                    }
                } catch (Throwable t) {
                    log.error("Error in Thread: ", t);
                } finally {
                    latch.countDown();
                }
            });
//            log.info("Thread {} have finished its operations", i);
        }

        
        assertTrue(latch.await(30, TimeUnit.SECONDS), "Threads did not finish in time");
        exec.shutdownNow();

        // Should have threads*iterations entries logged
        assertEquals(threads * iterations, testPacketLogger.getEntries().size());
    }
    
    @Test
    @Order(3)
    public void testLockingUnderConcurrentReadWrite() throws InterruptedException {
        // 1) I add a single rule that accepts any packet
        Rule acceptAll = new Rule("accept all", EDirection.INBOUND,
                                  new IPRange("0.0.0.0/0"), new IPRange("192.168.0.0/24"),
                                  new PortRange(0, 65535), new PortRange(0, 65535),
                                  EProtocol.TCP);
        firewall.updateActiveRuleSet(acceptAll, ETypeOfOperation.ADD, Optional.empty());
        
        int readers = 10;
        int writers = 5;
        int iterations = 500;
        ExecutorService exec = Executors.newFixedThreadPool(readers + writers);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(readers + writers);
        
        ConcurrentLinkedQueue<Throwable> exceptions = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Boolean> results = new ConcurrentLinkedQueue<>();

        // 2) Readers: keep calling processPacket
        for (int i = 0; i < readers; i++) {
            exec.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < iterations; j++) {
                        IPacket pkt = new PseudoPacket(new PseudoHeader("1.1.1.1", "192.168.0.140", j, 80, EProtocol.TCP), "");
                        // raccolgo i risultati
                        results.add(firewall.activeRuleSetProcessPacket(pkt));
                    }
                } catch (Throwable t) {
                    exceptions.add(t);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 3) Writers: alternate remove/add of the same rule
        for (int i = 0; i < writers; i++) {
            exec.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < iterations; j++) {
                        if(firewall.activeRuleSetContainsRule(acceptAll)){
                            firewall.updateActiveRuleSet(acceptAll, ETypeOfOperation.REMOVE, Optional.empty());
                        }
                        if(!firewall.activeRuleSetContainsRule(acceptAll)){
                            firewall.updateActiveRuleSet(acceptAll, ETypeOfOperation.ADD, Optional.empty());
                        } 
                    }
                } catch (Throwable t) {
                    exceptions.add(t);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();  // All threads start more or less together
        // 4) Waiting for all threads to finish their operations
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "Timeout threads");

        // 5) There must be no concurrency exceptions
        log.error("Exceptions collected during the test:");
        exceptions.forEach(e -> log.error(e.getLocalizedMessage()));
        log.info("The Active RuleSet actually have this rules: {}", firewall.getActiveRuleSet().getRules());
        assertTrue(exceptions.isEmpty(), "Exceptions emerged during the test");
        
        // 6) State invariance:
        //    after writers*iterations REMOVE+ADD alternations,
        //    the acceptAll rule must still be present only once
        assertTrue(firewall.activeRuleSetContainsRule(acceptAll), "The rule should still be there");
        assertEquals(1,firewall.getActiveRuleSetRules().stream().filter(r -> r.equals(acceptAll)).count(),"There must be no duplicates of the rule");
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
