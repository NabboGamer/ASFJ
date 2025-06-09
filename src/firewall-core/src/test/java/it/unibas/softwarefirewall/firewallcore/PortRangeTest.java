package it.unibas.softwarefirewall.firewallcore;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import it.unibas.softwarefirewall.firewallapi.IRange;

public class PortRangeTest {
    
    @Test
    @SuppressWarnings({"ResultOfObjectAllocationIgnored", "ThrowableResultIgnored"})
    public void testPortRange(){
        assertThrows(IllegalArgumentException.class, () -> { new PortRange(-1, 255); }, "Start must be grather than 0");
        assertThrows(IllegalArgumentException.class, () -> { new PortRange(0, 65536); }, "End must be less than 65535");
        assertThrows(IllegalArgumentException.class, () -> { new PortRange(65535, 0); }, "End must be grather than start");
        assertDoesNotThrow(() -> { new PortRange(10, 220); }, "The PortRange is well formed");
    }

    @Test
    public void testContains() {
        IRange<Integer> portRange = new PortRange(10, 220);
        assertTrue(portRange.contains(11));
        assertFalse(portRange.contains(221));
    }
    
}
