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

    private Injector injector;
    private InMemoryFirewallEngine firewall;
    private TestPacketLogger testPacketLogger;

    @BeforeAll
    public void setUp() {
        // I use a module that overrides PacketLogger with a mock one cause in this
        // test i want to test only the behavior of InMemoryFirewallEngine class
        injector = Guice.createInjector(new FirewallModule() {
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
        assertEquals(EDirection.OUTBOUND, firewall.getActiveRuleSet().getRules().get(0).getDirection());

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
        assertEquals("allow all secure", firewall.getActiveRuleSet().getRules().get(0).getDescription());
        
        
        //// 3. Test of the processPacket method
        // Create a packet to send to localhost
        IPacket pkt = new PseudoPacket(new PseudoHeader("1.2.3.4", "192.168.0.140", 1000, 80, EProtocol.TCP),"");
        boolean result = firewall.processPacket(pkt);
        assertTrue(result);
        // Verify that the logger has received a log
        assertEquals(1, testPacketLogger.getEntries().size());
        assertTrue(testPacketLogger.getEntries().get(0).getAllowed());
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
                        boolean ok = firewall.processPacket(pkt);
                        assertFalse(ok, "Expected packet to be denied due to empty rule set");
                    }
                } catch (Throwable t) {
                    log.error("Error in Thread: {}", t);
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
    
//    @Test
//    @Order(3)
//    public void testLockingUnderConcurrentReadWrite() throws InterruptedException {
//        // 1) Partiamo con una regola che blocca tutto
//        Rule denyAll = new Rule("r0", "deny all", Direction.INBOUND,
//            new IPRange("0.0.0.0/0"), new IPRange("127.0.0.1/32"),
//            new PortRange(0, 65535), new PortRange(0, 65535),
//            Protocol.TCP, false);
//        firewall.updateActiveRuleSet(denyAll, TypeOfOperation.ADD, Optional.empty());
//
//        int readers = 10;
//        int writers = 5;
//        int iterations = 500;
//        ExecutorService exec = Executors.newFixedThreadPool(readers + writers);
//        CountDownLatch done = new CountDownLatch(readers + writers);
//
//        // Collezioni thread-safe per raccogliere eventuali eccezioni o risultati strani
//        ConcurrentLinkedQueue<Throwable> exceptions = new ConcurrentLinkedQueue<>();
//        ConcurrentLinkedQueue<Boolean> results = new ConcurrentLinkedQueue<>();
//
//        // 2) Lettori: continuano a chiamare processPacket
//        for (int i = 0; i < readers; i++) {
//            exec.submit(() -> {
//                try {
//                    for (int j = 0; j < iterations; j++) {
//                        IPacket pkt = new PseudoPacket(
//                          "p"+j,
//                          new PseudoHeader("1.1.1.1","127.0.0.1",j,80,Protocol.TCP),
//                          ""
//                        );
//                        // raccolgo i risultati
//                        results.add(firewall.processPacket(pkt));
//                    }
//                } catch (Throwable t) {
//                    exceptions.add(t);
//                } finally {
//                    done.countDown();
//                }
//            });
//        }
//
//        // 3) Scrittori: alternano remove/add della stessa regola
//        for (int i = 0; i < writers; i++) {
//            exec.submit(() -> {
//                try {
//                    for (int j = 0; j < iterations; j++) {
//                        firewall.updateActiveRuleSet(denyAll, TypeOfOperation.REMOVE, Optional.empty());
//                        firewall.updateActiveRuleSet(denyAll, TypeOfOperation.ADD, Optional.empty());
//                    }
//                } catch (Throwable t) {
//                    exceptions.add(t);
//                } finally {
//                    done.countDown();
//                }
//            });
//        }
//
//        // 4) Attendo termine
//        assertTrue(done.await(30, TimeUnit.SECONDS), "Timeout threads");
//
//        // 5) Non devono esserci eccezioni di concorrenza
//        assertTrue(exceptions.isEmpty(), 
//            "Sono emerse eccezioni durante il test: " + exceptions);
//
//        // 6) Tutti i lettori dovrebbero aver visto o 'false' (assenza di regole) o 'false' (regola denyAll)
//        //    ovvero sempre false: nessun risultato true inatteso
//        assertTrue(results.stream().allMatch(b -> b == false),
//            "Lettori hanno visto qualche risultato true, comportamento incoerente");
//    }

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
