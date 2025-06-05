package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.Protocol;
import lombok.Data;

@Data
public class PseudoHeader implements IHeader {
    private final String sourceIP;
    private final String destinationIP;
    private final Integer sourcePort;
    private final Integer destinationPort;
    private final Protocol protocol;
}
