package it.unibas.softwarefirewall.firewallcore;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import it.unibas.softwarefirewall.firewallapi.IRange;

@Data
@Slf4j
public class IPRange implements IRange<String>, Cloneable {
    private IPAddress network;

    public IPRange(String cidr) throws IllegalArgumentException {
        this.network = new IPAddressString(cidr).getAddress();
        if (network == null || !network.isPrefixed()) {
            throw new IllegalArgumentException("Invalid CIDR notation: " + cidr);
        }
    }

    @Override
    public Boolean contains(String ip) {
        IPAddress ipAddress = new IPAddressString(ip).getAddress();
        return ipAddress != null && network.contains(ipAddress);
    }

    @Override
    public String toString() {
        return network.toCanonicalString();
    }
    
    @Override
    public Object clone(){
        try {
            IPRange clonedIPRange = (IPRange)super.clone();
            clonedIPRange.setNetwork(new IPAddressString(this.network.toString()).getAddress());
            return clonedIPRange;
        } catch (CloneNotSupportedException cnse) {
            log.error("Error not clonable object: ", cnse);
            return null;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        IPRange otherIPRange = (IPRange) o;
        return network.equals(otherIPRange.network);
    }

    @Override
    public int hashCode() {
        return network.hashCode();
    }

}
