package it.unibas.softwarefirewall.firewallapi;

public interface Range<T> {
    Boolean contains(T value);
}