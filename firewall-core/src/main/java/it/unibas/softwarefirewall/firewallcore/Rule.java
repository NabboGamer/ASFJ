package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.Protocol;
import it.unibas.softwarefirewall.firewallapi.Range;
import lombok.Data;

@Data
public class Rule implements IRule {
    private String ID;
    private String description;
    private Range<String> sourceIPRange;
    private Range<String> destinationIPRange;
    private Range<Integer> sourcePortRange;
    private Range<Integer> destinationPortRange;
    private Protocol protocol;
    private Boolean allowed;

    @Override
    public Boolean matches(IPacket packet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
