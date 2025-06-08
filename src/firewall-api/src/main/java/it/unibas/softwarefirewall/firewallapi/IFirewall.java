package it.unibas.softwarefirewall.firewallapi;

public interface IFirewall {
    IRuleSet getActiveRuleSet();
    IRuleSet getClonedRuleSetUnderTest();
    void updateActiveRuleSet(IRule rule, TypeOfOperation typeOfOperation);
    void updateClonedRuleSetUnderTest(IRule rule, TypeOfOperation typeOfOperation);
    Boolean inspect(IPacket packet);
}