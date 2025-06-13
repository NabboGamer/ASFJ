package it.unibas.softwarefirewall.firewallapi;

import java.util.List;
import java.util.Optional;

public interface IFirewallFacade {
    IRuleSet getActiveRuleSet();
    List<IRule> getActiveRuleSetRules();
    Boolean activeRuleSetContainsRule(IRule rule);
    IRuleSet getClonedRuleSetUnderTest();
    List<IRule> getClonedRuleSetUnderTestRules();
    Boolean clonedRuleSetUnderTestContainsRule(IRule rule);
    void updateActiveRuleSet(IRule rule, ETypeOfOperation typeOfOperation, Optional<IRule> otherRule);
    void updateClonedRuleSetUnderTest(IRule rule, ETypeOfOperation typeOfOperation, Optional<IRule> otherRule);
    Boolean processPacket(IPacket packet);
}