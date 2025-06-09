package it.unibas.softwarefirewall.firewallapi;

public interface IRule {
    String getID();
    String getDescription();
    IRange<String> getSourceIPRange();
    IRange<String> getDestinationIPRange();
    IRange<Integer> getSourcePortRange();
    IRange<Integer> getDestinationPortRange();
    EProtocol getProtocol();
    EDirection getDirection();
    Boolean matches(IPacket packet);
    Object clone();
}