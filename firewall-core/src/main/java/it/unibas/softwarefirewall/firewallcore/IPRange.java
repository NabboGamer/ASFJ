package it.unibas.softwarefirewall.firewallcore;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import it.unibas.softwarefirewall.firewallapi.Range;
import lombok.Data;

@Data
public class IPRange implements Range<String> {
    private IPAddress network;

    public IPRange(String cidr) {
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
}
