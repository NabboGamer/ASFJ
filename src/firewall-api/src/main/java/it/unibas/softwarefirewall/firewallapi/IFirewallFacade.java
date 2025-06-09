package it.unibas.softwarefirewall.firewallapi;

public interface IFirewallFacade {
    IRuleSet getActiveRuleSet();
    IRuleSet getClonedRuleSetUnderTest();
    void updateActiveRuleSet(IRule rule, ETypeOfOperation typeOfOperation);
    void updateClonedRuleSetUnderTest(IRule rule, ETypeOfOperation typeOfOperation);
    Boolean inspect(IPacket packet);
}