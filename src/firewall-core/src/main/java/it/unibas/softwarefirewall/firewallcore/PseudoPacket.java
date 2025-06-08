package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PseudoPacket implements IPacket {
    private final String ID;
    private final IHeader header;
    private final String payload;
//    private final String checksum;
}
