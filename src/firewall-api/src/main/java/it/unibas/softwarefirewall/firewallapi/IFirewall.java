package it.unibas.softwarefirewall.firewallapi;

public interface IFirewall {
    IRuleSet getCurrentRuleSet();
    void setRuleSet(IRuleSet ruleSet);
    Boolean inspect(IPacket packet);
}