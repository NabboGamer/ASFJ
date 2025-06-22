package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import java.util.UUID;
import lombok.Data;

@Data
public class PseudoPacket implements IPacket {
    private final String ID;
    private final IHeader header;
    private final String payload;
//    private final String checksum;
    
    public PseudoPacket(IHeader header, String payload){
        this.ID = UUID.randomUUID().toString();
        this.header = header;
        this.payload = payload;
    }
    
    public String toDisplayString() {
        IHeader h = this.getHeader();
        return String.format("[%s] [%s:%d → %s:%d] Payload: \"%s\"", h.getProtocol(), h.getSourceIP(), 
                                                                     h.getSourcePort(), h.getDestinationIP(), 
                                                                     h.getDestinationPort(), this.getPayload());
    }
    
}
