package it.unibas.softwarefirewall.firewallapi;

public interface IRule {
    String getID();
    String getDescription();
    Range<String> getSourceIPRange();
    Range<String> getDestinationIPRange();
    Range<Integer> getSourcePortRange();
    Range<Integer> getDestinationPortRange();
    Protocol getProtocol();
    Direction getDirection();
    Boolean matches(IPacket packet);
    Object clone();
}