package it.unibas.softwarefirewall.firewallapi;

public interface IHeader {
    String getSourceIP();
    String getDestinationIP();
    Integer getSourcePort();
    Integer getDestinationPort();
    Protocol getProtocol();
}
