package it.unibas.softwarefirewall.firewallapi;

public interface IPacket {
    String getID();
    IHeader getHeader();
    String getPayload();
    String toDisplayString();
//    String getChecksum();
}