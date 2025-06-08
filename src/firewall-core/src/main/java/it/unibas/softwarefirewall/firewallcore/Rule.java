package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.Direction;
import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.Protocol;
import it.unibas.softwarefirewall.firewallapi.Range;
import java.net.UnknownHostException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Rule implements IRule, Cloneable {

    private String ID;
    private String description;
    private Direction direction;
    private Range<String> sourceIPRange;
    private Range<String> destinationIPRange;
    private Range<Integer> sourcePortRange;
    private Range<Integer> destinationPortRange;
    private Protocol protocol;

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

        if (this.direction.equals(Direction.INBOUND)) {
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

        return (this.description != null ? description.equals(otherRule.description) : otherRule.description == null) &&
               (this.direction == otherRule.direction) &&
               (this.sourceIPRange != null ? sourceIPRange.equals(otherRule.sourceIPRange) : otherRule.sourceIPRange == null) &&
               (this.destinationIPRange != null ? destinationIPRange.equals(otherRule.destinationIPRange) : otherRule.destinationIPRange == null) &&
               (this.sourcePortRange != null ? sourcePortRange.equals(otherRule.sourcePortRange) : otherRule.sourcePortRange == null) &&
               (this.destinationPortRange != null ? destinationPortRange.equals(otherRule.destinationPortRange) : otherRule.destinationPortRange == null) &&
               (this.protocol == otherRule.protocol);
    }

    @Override
    public int hashCode() {
        int result = (description != null ? description.hashCode() : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (sourceIPRange != null ? sourceIPRange.hashCode() : 0);
        result = 31 * result + (destinationIPRange != null ? destinationIPRange.hashCode() : 0);
        result = 31 * result + (sourcePortRange != null ? sourcePortRange.hashCode() : 0);
        result = 31 * result + (destinationPortRange != null ? destinationPortRange.hashCode() : 0);
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        return result;
    }

}
