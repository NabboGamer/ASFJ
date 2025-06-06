package it.unibas.softwarefirewall.firewallcore;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
