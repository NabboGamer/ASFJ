package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.EDirection;
import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.EProtocol;
import java.net.UnknownHostException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import it.unibas.softwarefirewall.firewallapi.IRange;
import java.util.UUID;

@Data
@NoArgsConstructor
@Slf4j
public class Rule implements IRule, Cloneable {

    private String ID;
    private String description;
    private EDirection direction;
    private IRange<String> sourceIPRange;
    private IRange<String> destinationIPRange;
    private IRange<Integer> sourcePortRange;
    private IRange<Integer> destinationPortRange;
    private EProtocol protocol;
    
    public Rule(String description, EDirection direction, 
                IRange<String> sourceIPRange, IRange<String> destinationIPRange, 
                IRange<Integer> sourcePortRange, IRange<Integer> destinationPortRange, 
                EProtocol protocol){
        this.ID = UUID.randomUUID().toString();
        this.description = description;
        this.direction = direction;
        this.sourceIPRange = sourceIPRange;
        this.destinationIPRange = destinationIPRange;
        this.sourcePortRange = sourcePortRange;
        this.destinationPortRange = destinationPortRange;
        this.protocol = protocol;
    }

    @Override
    public Boolean matches(IPacket packet) {
        IHeader header = packet.getHeader();

        String localIpAddress;
        try {
            localIpAddress = NetworkUtils.getLocalIpAddress();
        } catch (UnknownHostException ex) {
            log.error(ex.getLocalizedMessage());
            return false;
        }

        if (this.direction.equals(EDirection.INBOUND)) {
            if (header.getDestinationIP().equals(localIpAddress)) {
                return evaluateMatch(header);
            } else {
                return false;
            }
        } else {
            if (header.getSourceIP().equals(localIpAddress)) {
                return evaluateMatch(header);
            } else {
                return false;
            }
        }
    }

    private Boolean evaluateMatch(IHeader header) {
        Boolean isSourceIPInRange = this.sourceIPRange.contains(header.getSourceIP());
        Boolean isDestinationIPInRange = this.destinationIPRange.contains(header.getDestinationIP());
        Boolean isSorucePortInRange = this.sourcePortRange.contains(header.getSourcePort());
        Boolean isDestinationPortInRange = this.destinationPortRange.contains(header.getDestinationPort());
        Boolean isSameProtocol = this.protocol.equals(header.getProtocol());
        return isSourceIPInRange && isDestinationIPInRange && isSorucePortInRange && isDestinationPortInRange && isSameProtocol;
    }
    
    @Override
    public Object clone(){
        try {
            Rule clonedRule = (Rule)super.clone();
            clonedRule.setID(this.ID);
            clonedRule.setDescription(this.description);
            clonedRule.setSourceIPRange((IPRange)this.sourceIPRange.clone());
            clonedRule.setDestinationIPRange((IPRange)this.destinationIPRange.clone());
            clonedRule.setSourcePortRange((PortRange)this.sourcePortRange.clone());
            clonedRule.setDestinationPortRange((PortRange)this.destinationPortRange.clone());
            clonedRule.setProtocol(this.getProtocol());
            return clonedRule;
        } catch (CloneNotSupportedException cnse) {
            log.error("Error: not clonable object: {}", cnse);
            return null;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Rule otherRule = (Rule) o;
                
        return (this.ID.equals(otherRule.ID));
    }

    @Override
    public int hashCode() {
        return ID != null ? ID.hashCode() : 0;
    }

}
