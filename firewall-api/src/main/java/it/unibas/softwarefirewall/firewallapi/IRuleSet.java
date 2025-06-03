package it.unibas.softwarefirewall.firewallapi;

import java.util.List;

public interface IRuleSet {
    List<IRule> getRules();
    void setRules(List<IRule> newRules);
    void addRule(IRule newRule);
    void removeRule(IRule rule);
    Object clone();
    Boolean matches(IPacket packet);
}