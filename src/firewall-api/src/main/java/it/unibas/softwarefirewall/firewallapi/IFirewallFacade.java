package it.unibas.softwarefirewall.firewallapi;

import java.util.Optional;

public interface IFirewallFacade {
    IRuleSet getActiveRuleSet();
    IRuleSet getClonedRuleSetUnderTest();
    void updateActiveRuleSet(IRule rule, ETypeOfOperation typeOfOperation, Optional<IRule> otherRule) throws IllegalArgumentException;
    void updateClonedRuleSetUnderTest(IRule rule, ETypeOfOperation typeOfOperation, Optional<IRule> otherRule) throws IllegalArgumentException;
    Boolean processPacket(IPacket packet);
}