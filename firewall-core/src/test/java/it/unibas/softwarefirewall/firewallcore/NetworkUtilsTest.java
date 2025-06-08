package it.unibas.softwarefirewall.firewallcore;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//N.B: Since in Rule in the case of an OUTBUND Direction the verification occurs that the sourceIP
//     of the packet header actually corresponds to the local IP, if this IP changes because
//     you are connected to another network the packet will not pass and the test will fail!
//     Obviously the same thing occur in case of INBOUND Direction and destinationIP.
//     This test class is built with IPs compliant with my development environment.
//     To re-run the test methods in your environment and be sure that they work, 
//     keep in mind what was said previously and appropriately change the harcoded IPs
//     of the pesuedo-packets and the harcoded CIDRs of the rules.
@Slf4j
public class NetworkUtilsTest {

    @Test
    public void testGetLocalIpAddress() throws Exception {
        String localIpAddress = NetworkUtils.getLocalIpAddress();
        log.debug("The assigned IP Address for this machine is: {}", localIpAddress);
        assertDoesNotThrow(() -> { NetworkUtils.getLocalIpAddress();} , "The local host name could be resolved into an address");
        assertEquals(localIpAddress, "192.168.0.140", "The current network interface with which I am connected to the internet is Wi-Fi and checking with the command ps1 \"NetIPAddress\" returns this value");
    }
    
}
