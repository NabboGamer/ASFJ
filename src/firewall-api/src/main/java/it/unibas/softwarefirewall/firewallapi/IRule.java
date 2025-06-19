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
    void setDescription(String description);
    void setSourceIPRange(IRange<String> sourceIPRange);
    void setDestinationIPRange(IRange<String> destinationIPRange);
    void setSourcePortRange(IRange<Integer> sourcePortRange);
    void setDestinationPortRange(IRange<Integer> destinationPortRange);
    void setProtocol(EProtocol protocol);
    void setDirection(EDirection direction);
    Boolean matches(IPacket packet);
    Object clone();
}