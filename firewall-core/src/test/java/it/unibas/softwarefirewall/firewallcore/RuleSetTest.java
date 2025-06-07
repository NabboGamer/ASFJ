package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.Direction;
import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.IRuleSet;
import it.unibas.softwarefirewall.firewallapi.Protocol;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class RuleSetTest {
    
    private IHeader pseudoHeader1;
    private IPacket pseudoPacket1;
    private IRule testRule1;
    private IRule testRule2;
    private IRule testRule3;
    private IRule testRule4;

    @BeforeAll
    public void setUp(){
        this.testRule1 = new Rule("1", "First testing rule", Direction.INBOUND,
                                  new IPRange("193.204.19.0/24"), new IPRange("192.168.0.0/24"),
                                  new PortRange(10, 100), new PortRange(10, 100),
                                  Protocol.TCP);
        
        this.testRule2 = new Rule("2", "Second testing rule", Direction.INBOUND,
                                  new IPRange("192.168.0.0/24"), new IPRange("10.0.0.0/8"),
                                  new PortRange(10, 100), new PortRange(10, 100),
                                  Protocol.UDP);
        
        this.testRule3 = new Rule("3", "Third testing rule", Direction.OUTBOUND,
                                  new IPRange("193.204.19.0/24"), new IPRange("192.168.0.0/24"),
                                  new PortRange(10, 100), new PortRange(10, 100),
                                  Protocol.TCP);
        
        this.testRule4 = new Rule("4", "Allow outgoing ssh connections for devices of this internal network", Direction.OUTBOUND,
                                  new IPRange("192.168.0.0/24"), new IPRange("0.0.0.0/0"),
                                  new PortRange(22, 22), new PortRange(22, 22),
                                  Protocol.TCP);
        
        this.pseudoHeader1 = new PseudoHeader("192.168.0.140", "150.3.2.2", 22 , 22, Protocol.TCP);
        this.pseudoPacket1 = new PseudoPacket(UUID.randomUUID().toString(), this.pseudoHeader1, "Pseudo-packet 1 send during ssh handshake");
    }
    
    @Test
    public void testMatches() {
        IRuleSet testRuleSet = new RuleSet(new ArrayList<>());
        testRuleSet.addRule(testRule1);
        testRuleSet.addRule(testRule2);
        testRuleSet.addRule(testRule3);
        assertFalse(testRuleSet.matches(pseudoPacket1));
        
        testRuleSet.addRule(testRule4);
        assertTrue(testRuleSet.matches(pseudoPacket1));
    }
    
    
    @Test
    public void testClone(){
        IRuleSet testRuleSet = new RuleSet(new ArrayList<>());
        testRuleSet.addRule(testRule1);
        RuleSet clonedTestRuleSet = (RuleSet)testRuleSet.clone();
        
        // Verifico che il clone non sia nullo e 
        // verifico che il clone e l'oggetto originale non coincidano
        assertNotNull(clonedTestRuleSet);
        assertNotSame(clonedTestRuleSet, testRuleSet);
        
        // Controllo delle proprietà
        List<IRule> testRulesSetRules = testRuleSet.getRules();
        List<IRule> clonedTestRulesSetRules = clonedTestRuleSet.getRules();
        for (int i = 0; i < testRulesSetRules.size(); i++) {
            assertNotNull(clonedTestRulesSetRules.get(i));
            assertNotSame(testRulesSetRules.get(i), clonedTestRulesSetRules.get(i));
            assertTrue(testRulesSetRules.get(i).equals(clonedTestRulesSetRules.get(i)));
        }
        
        log.debug("Test RuleSet: {}", testRuleSet.toString());
        log.debug("Cloned RuleSet: {}", clonedTestRuleSet.toString());
    }
    
    @Test
    public void testLoadRuleSetFromFile() {
        IRuleSet testRuleSet = new RuleSet(new ArrayList<>());
        testRuleSet.loadRuleSetFromFile();
        log.debug("RuleSet loaded from JSON file: {}", testRuleSet.toString());
    }
}
