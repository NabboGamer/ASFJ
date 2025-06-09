package it.unibas.softwarefirewall.firewallapi;

public interface IRange<T> {
    Boolean contains(T value);
    Object clone();
}