package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.EDirection;
import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.EProtocol;
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
//     keep in mind what was said previously and appropriately change the hardcoded IPs
//     of the pseudo-packets and the hardcoded CIDRs of the rules.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RuleTest {

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
        IHeader pseudoHeader1 = new PseudoHeader("193.204.19.174", "192.168.0.140", 22, 22, EProtocol.TCP); // ALLOWED
        IHeader pseudoHeader2 = new PseudoHeader("193.204.20.174", "192.168.0.140", 22, 22, EProtocol.TCP); // SourceIP not in
        IHeader pseudoHeader3 = new PseudoHeader("193.204.19.174", "192.168.1.140", 22, 22, EProtocol.TCP); // DestinationIP not in
        IHeader pseudoHeader4 = new PseudoHeader("193.204.19.174", "192.168.0.140", 500, 22, EProtocol.TCP);// SorucePort not in
        IHeader pseudoHeader5 = new PseudoHeader("193.204.19.174", "192.168.0.140", 22, 500, EProtocol.TCP);// DestinationPort not in
        IHeader pseudoHeader6 = new PseudoHeader("193.204.19.174", "192.168.0.140", 22, 22, EProtocol.UDP); // Different Protocol
        IHeader pseudoHeader7 = new PseudoHeader("192.168.0.140", "10.0.0.1", 80, 80, EProtocol.TCP);       // ALLOWED
        IHeader pseudoHeader8 = new PseudoHeader("10.0.0.1", "192.168.0.140", 80, 80, EProtocol.TCP);       // Incompatible source with this Direction

        this.pseudoPacket1 = new PseudoPacket(pseudoHeader1, "Pseudo-packet 1 generated for testing purpose");
        this.pseudoPacket2 = new PseudoPacket(pseudoHeader2, "Pseudo-packet 2 generated for testing purpose");
        this.pseudoPacket3 = new PseudoPacket(pseudoHeader3, "Pseudo-packet 3 generated for testing purpose");
        this.pseudoPacket4 = new PseudoPacket(pseudoHeader4, "Pseudo-packet 4 generated for testing purpose");
        this.pseudoPacket5 = new PseudoPacket(pseudoHeader5, "Pseudo-packet 5 generated for testing purpose");
        this.pseudoPacket6 = new PseudoPacket(pseudoHeader6, "Pseudo-packet 6 generated for testing purpose");
        this.pseudoPacket7 = new PseudoPacket(pseudoHeader7, "Pseudo-packet 7 generated for testing purpose");
        this.pseudoPacket8 = new PseudoPacket(pseudoHeader8, "Pseudo-packet 8 generated for testing purpose");
        
        this.testRule1 = new Rule("First testing rule", EDirection.INBOUND, 
                                  new IPRange("193.204.19.0/24"), new IPRange("192.168.0.0/24"),
                                  new PortRange(10, 100), new PortRange(10, 100), 
                                  EProtocol.TCP);
        
        this.testRule2 = new Rule("Second testing rule", EDirection.OUTBOUND, 
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
