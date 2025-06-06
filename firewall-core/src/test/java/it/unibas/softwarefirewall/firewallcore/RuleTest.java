package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.Direction;
import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.Protocol;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RuleTest {
    
    IHeader pseudoHeader1;
    IHeader pseudoHeader2;
    IHeader pseudoHeader3;
    IHeader pseudoHeader4;
    IHeader pseudoHeader5;
    IHeader pseudoHeader6;
    IHeader pseudoHeader7;
    IHeader pseudoHeader8;
    
    IPacket pseudoPacket1;
    IPacket pseudoPacket2;
    IPacket pseudoPacket3;
    IPacket pseudoPacket4;
    IPacket pseudoPacket5;
    IPacket pseudoPacket6;
    IPacket pseudoPacket7;
    IPacket pseudoPacket8;
    
    IRule testRule1;
    IRule testRule2;
    
    @BeforeAll
    public void setUp(){
        this.pseudoHeader1 = new PseudoHeader("193.204.19.174", "192.168.0.140", 22 , 22, Protocol.TCP); // ALLOWED
        this.pseudoHeader2 = new PseudoHeader("193.204.20.174", "192.168.0.140", 22 , 22, Protocol.TCP); // SourceIP not in
        this.pseudoHeader3 = new PseudoHeader("193.204.19.174", "192.168.1.140", 22 , 22, Protocol.TCP); // DestinationIP not in
        this.pseudoHeader4 = new PseudoHeader("193.204.19.174", "192.168.0.140", 500 , 22, Protocol.TCP);// SorucePort not in
        this.pseudoHeader5 = new PseudoHeader("193.204.19.174", "192.168.0.140", 22 , 500, Protocol.TCP);// DestinationPort not in
        this.pseudoHeader6 = new PseudoHeader("193.204.19.174", "192.168.0.140", 22 , 22, Protocol.UDP); // Different Protocol
        this.pseudoHeader7 = new PseudoHeader("192.168.0.140", "10.0.0.1", 80 , 80, Protocol.TCP);       // ALLOWED
        this.pseudoHeader8 = new PseudoHeader("10.0.0.1", "192.168.0.140", 80 , 80, Protocol.TCP);       // Incompatible source with this Direction
        
        this.pseudoPacket1 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader1, "Pseudo-packet 1 generated for testing purpose");
        this.pseudoPacket2 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader2, "Pseudo-packet 2 generated for testing purpose");
        this.pseudoPacket3 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader3, "Pseudo-packet 3 generated for testing purpose");
        this.pseudoPacket4 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader4, "Pseudo-packet 4 generated for testing purpose");
        this.pseudoPacket5 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader5, "Pseudo-packet 5 generated for testing purpose");
        this.pseudoPacket6 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader6, "Pseudo-packet 6 generated for testing purpose");
        this.pseudoPacket7 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader7, "Pseudo-packet 7 generated for testing purpose");
        this.pseudoPacket8 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader8, "Pseudo-packet 8 generated for testing purpose");
        
        this.testRule1 = new Rule("1", "First testing rule", Direction.INBOUND, 
                                  new IPRange("193.204.19.0/24"), new IPRange("192.168.0.0/24"),
                                  new PortRange(10, 100), new PortRange(10, 100), 
                                  Protocol.TCP);
        
        this.testRule2 = new Rule("2", "Second testing rule", Direction.OUTBOUND, 
                                  new IPRange("192.168.0.0/24"), new IPRange("10.0.0.0/8"),
                                  new PortRange(10, 100), new PortRange(10, 100), 
                                  Protocol.TCP);
    }
    
    @Test
    public void testMatches1(){
        assertTrue(testRule1.matches(pseudoPacket1));
        assertFalse(testRule1.matches(pseudoPacket2));
        assertFalse(testRule1.matches(pseudoPacket3));
        assertFalse(testRule1.matches(pseudoPacket4));
        assertFalse(testRule1.matches(pseudoPacket5));
        assertFalse(testRule1.matches(pseudoPacket6));
    }
    
    @Test
    public void testMatches2(){
        assertTrue(testRule2.matches(pseudoPacket7));
        assertFalse(testRule2.matches(pseudoPacket8));
    }

}
