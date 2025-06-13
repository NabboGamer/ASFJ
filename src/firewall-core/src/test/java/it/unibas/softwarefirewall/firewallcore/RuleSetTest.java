package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.EDirection;
import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.IRuleSet;
import it.unibas.softwarefirewall.firewallapi.EProtocol;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

//N.B: Since in Rule in the case of an OUTBUND Direction the verification occurs that the sourceIP
//     of the packet header actually corresponds to the local IP, if this IP changes because
//     you are connected to another network the packet will not pass and the test will fail!
//     Obviously the same thing occur in case of INBOUND Direction and destinationIP.
//     This test class is built with IPs compliant with my development environment.
//     To re-run the test methods in your environment and be sure that they work, 
//     keep in mind what was said previously and appropriately change the harcoded IPs
//     of the pesuedo-packets and the harcoded CIDRs of the rules.
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
        this.testRule1 = new Rule("First testing rule", EDirection.INBOUND,
                                  new IPRange("193.204.19.0/24"), new IPRange("192.168.0.0/24"),
                                  new PortRange(10, 100), new PortRange(10, 100),
                                  EProtocol.TCP);
        
        this.testRule2 = new Rule("Second testing rule", EDirection.INBOUND,
                                  new IPRange("192.168.0.0/24"), new IPRange("10.0.0.0/8"),
                                  new PortRange(10, 100), new PortRange(10, 100),
                                  EProtocol.UDP);
        
        this.testRule3 = new Rule("Third testing rule", EDirection.OUTBOUND,
                                  new IPRange("193.204.19.0/24"), new IPRange("192.168.0.0/24"),
                                  new PortRange(10, 100), new PortRange(10, 100),
                                  EProtocol.TCP);
        
        this.testRule4 = new Rule("Allow outgoing ssh connections for devices of this internal network", EDirection.OUTBOUND,
                                  new IPRange("192.168.0.0/24"), new IPRange("0.0.0.0/0"),
                                  new PortRange(22, 22), new PortRange(22, 22),
                                  EProtocol.TCP);
        
        this.pseudoHeader1 = new PseudoHeader("192.168.0.140", "150.3.2.2", 22 , 22, EProtocol.TCP);
        this.pseudoPacket1 = new PseudoPacket(this.pseudoHeader1, "Pseudo-packet 1 send during ssh handshake");
    }
    
    @Test
    public void testMatches() {
        IRuleSet testRuleSet = new RuleSet(new RuleSetLoaderJsonStrategy());
        testRuleSet.addRule(testRule1);
        testRuleSet.addRule(testRule2);
        testRuleSet.addRule(testRule3);
        assertFalse(testRuleSet.matches(pseudoPacket1));
        
        testRuleSet.addRule(testRule4);
        assertTrue(testRuleSet.matches(pseudoPacket1));
    }
    
    
    @Test
    public void testClone(){
        IRuleSet testRuleSet = new RuleSet(new RuleSetLoaderJsonStrategy());
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
        IRuleSet testRuleSet = new RuleSet(new RuleSetLoaderJsonStrategy());
        log.debug("RuleSet loaded from JSON file: {}", testRuleSet.toString());
        assertEquals(testRuleSet.getRules().size(), 2);
    }
}
