package it.unibas.softwarefirewall.firewallcore;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import it.unibas.softwarefirewall.firewallapi.IRange;

public class IPRangeTest {
    
    @Test
    @SuppressWarnings({"ResultOfObjectAllocationIgnored", "ThrowableResultIgnored"})
    public void testIPRange(){
        assertThrows(IllegalArgumentException.class, () -> new IPRange("192.168.0.0"), "Invalid CIDR format");
        assertThrows(IllegalArgumentException.class, () -> new IPRange("test"), "Invalid IP string");
        assertThrows(IllegalArgumentException.class, () -> new IPRange("192.168.1.257"), "Invalid IP string");
        assertDoesNotThrow(() -> {  new IPRange("192.168.0.0/24"); }, "Valid CIDR");
    }
    
    @Test
    public void testContains(){
        IRange<String> ipRange = new IPRange("192.168.0.0/24");
        assertTrue(ipRange.contains("192.168.0.1"), "IP is contained in the IPRange");
        assertTrue(ipRange.contains("192.168.0.255"), "IP is contained in the IPRange");
        assertFalse(ipRange.contains("192.168.1.255"), "IP is not contained in the IPRange");
        assertFalse(ipRange.contains("10.0.0.5"), "IP is not contained in the IPRange");
        
    }
    
}
