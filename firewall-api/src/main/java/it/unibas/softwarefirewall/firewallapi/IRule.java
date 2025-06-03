package it.unibas.softwarefirewall.firewallapi;

public interface IRule {
    String getID();
    String getDescription();
    Range<String> getSourceIpRange();
    Range<String> getDestinationIpRange();
    Range<Integer> getSourcePortRange();
    Range<Integer> getDestinationPortRange();
    Protocol getProtocol();
    Boolean isAllowed();
    Boolean matches(IPacket packet);
}