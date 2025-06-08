package it.unibas.softwarefirewall.firewallcore;

import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetworkUtils {

    public static String getLocalIpAddress() throws UnknownHostException {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            throw new UnknownHostException("Unable to get local IP address");
        }
    }
}

