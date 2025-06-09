package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.EDirection;
import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.EProtocol;
import java.util.UUID;
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
public class RuleTest {
    
    private IHeader pseudoHeader1;
    private IHeader pseudoHeader2;
    private IHeader pseudoHeader3;
    private IHeader pseudoHeader4;
    private IHeader pseudoHeader5;
    private IHeader pseudoHeader6;
    private IHeader pseudoHeader7;
    private IHeader pseudoHeader8;
    
    private IPacket pseudoPacket1;
    private IPacket pseudoPacket2;
    private IPacket pseudoPacket3;
    private IPacket pseudoPacket4;
    private IPacket pseudoPacket5;
    private IPacket pseudoPacket6;
    private IPacket pseudoPacket7;
    private IPacket pseudoPacket8;
    
    private IRule testRule1;
    private IRule testRule2;
    
    @BeforeAll
    public void setUp(){
        this.pseudoHeader1 = new PseudoHeader("193.204.19.174", "192.168.0.140", 22 , 22, EProtocol.TCP); // ALLOWED
        this.pseudoHeader2 = new PseudoHeader("193.204.20.174", "192.168.0.140", 22 , 22, EProtocol.TCP); // SourceIP not in
        this.pseudoHeader3 = new PseudoHeader("193.204.19.174", "192.168.1.140", 22 , 22, EProtocol.TCP); // DestinationIP not in
        this.pseudoHeader4 = new PseudoHeader("193.204.19.174", "192.168.0.140", 500 , 22, EProtocol.TCP);// SorucePort not in
        this.pseudoHeader5 = new PseudoHeader("193.204.19.174", "192.168.0.140", 22 , 500, EProtocol.TCP);// DestinationPort not in
        this.pseudoHeader6 = new PseudoHeader("193.204.19.174", "192.168.0.140", 22 , 22, EProtocol.UDP); // Different Protocol
        this.pseudoHeader7 = new PseudoHeader("192.168.0.140", "10.0.0.1", 80 , 80, EProtocol.TCP);       // ALLOWED
        this.pseudoHeader8 = new PseudoHeader("10.0.0.1", "192.168.0.140", 80 , 80, EProtocol.TCP);       // Incompatible source with this Direction
        
        this.pseudoPacket1 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader1, "Pseudo-packet 1 generated for testing purpose");
        this.pseudoPacket2 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader2, "Pseudo-packet 2 generated for testing purpose");
        this.pseudoPacket3 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader3, "Pseudo-packet 3 generated for testing purpose");
        this.pseudoPacket4 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader4, "Pseudo-packet 4 generated for testing purpose");
        this.pseudoPacket5 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader5, "Pseudo-packet 5 generated for testing purpose");
        this.pseudoPacket6 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader6, "Pseudo-packet 6 generated for testing purpose");
        this.pseudoPacket7 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader7, "Pseudo-packet 7 generated for testing purpose");
        this.pseudoPacket8 = new PseudoPacket(UUID.randomUUID().toString(), pseudoHeader8, "Pseudo-packet 8 generated for testing purpose");
        
        this.testRule1 = new Rule("1", "First testing rule", EDirection.INBOUND, 
                                  new IPRange("193.204.19.0/24"), new IPRange("192.168.0.0/24"),
                                  new PortRange(10, 100), new PortRange(10, 100), 
                                  EProtocol.TCP);
        
        this.testRule2 = new Rule("2", "Second testing rule", EDirection.OUTBOUND, 
                                  new IPRange("192.168.0.0/24"), new IPRange("10.0.0.0/8"),
                                  new PortRange(10, 100), new PortRange(10, 100), 
                                  EProtocol.TCP);
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
