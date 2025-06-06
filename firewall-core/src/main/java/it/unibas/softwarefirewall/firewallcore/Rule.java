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
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j
public class Rule implements IRule {

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

}
