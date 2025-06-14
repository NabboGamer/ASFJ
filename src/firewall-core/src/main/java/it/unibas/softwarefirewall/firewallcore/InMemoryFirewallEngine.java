package it.unibas.softwarefirewall.firewallcore;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.IRuleSet;
import it.unibas.softwarefirewall.firewallapi.ETypeOfOperation;
import static it.unibas.softwarefirewall.firewallapi.ETypeOfOperation.ADD;
import static it.unibas.softwarefirewall.firewallapi.ETypeOfOperation.REMOVE;
import static it.unibas.softwarefirewall.firewallapi.ETypeOfOperation.UPDATE;
import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class InMemoryFirewallEngine implements IFirewallFacade {
    
    private IRuleSet activeRuleSet;
    private IRuleSet clonedRuleSetUnderTest = null;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    @Inject
    public InMemoryFirewallEngine(IRuleSet ruleSet) {
        this.activeRuleSet = ruleSet;
    }

    @Override
    public IRuleSet getActiveRuleSet() {
        this.rwLock.readLock().lock();
        try {
            return this.activeRuleSet;
        } finally {
            this.rwLock.readLock().unlock();
        }
    }
    
    @Override
    public List<IRule> getActiveRuleSetRules() {
        this.rwLock.readLock().lock();
        try {
            return this.activeRuleSet.getRules();
        } finally {
            this.rwLock.readLock().unlock();
        }
    }
    
    @Override
    public Boolean activeRuleSetContainsRule(IRule rule) {
        rwLock.readLock().lock();
        try {
            return activeRuleSet.getRules().contains(rule);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public IRuleSet getClonedRuleSetUnderTest(){
        this.rwLock.readLock().lock();
        try {
            if (this.clonedRuleSetUnderTest == null) {
                this.clonedRuleSetUnderTest = (IRuleSet) this.activeRuleSet.clone();
            }
            return this.clonedRuleSetUnderTest;
        } finally {
            this.rwLock.readLock().unlock();
        }
    }
    
    @Override
    public List<IRule> getClonedRuleSetUnderTestRules(){
        this.rwLock.readLock().lock();
        try {
            if (this.clonedRuleSetUnderTest == null) {
                this.clonedRuleSetUnderTest = (IRuleSet) this.activeRuleSet.clone();
            }
            return this.clonedRuleSetUnderTest.getRules();
        } finally {
            this.rwLock.readLock().unlock();
        }
    }
    
    @Override
    public Boolean clonedRuleSetUnderTestContainsRule(IRule rule) {
        rwLock.readLock().lock();
        try {
            if (this.clonedRuleSetUnderTest == null) {
                this.clonedRuleSetUnderTest = (IRuleSet) this.activeRuleSet.clone();
            }
            return clonedRuleSetUnderTest.getRules().contains(rule);
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    @Override
    public void updateActiveRuleSet(IRule rule, ETypeOfOperation op, Optional<IRule> otherRule) {
        applyUpdate(rule, op, otherRule, this.activeRuleSet, /* resetClone= */true);
    }

    @Override
    public void updateClonedRuleSetUnderTest(IRule rule, ETypeOfOperation op, Optional<IRule> otherRule) {
        applyUpdate(rule, op, otherRule, this.clonedRuleSetUnderTest, /* resetClone= */false);
    }

    /* 
    * Performs ADD/REMOVE/UPDATE operations on a RuleSet of your choice and,
    * only if resetClone==true invalidates the clone of the activeRuleSet
    */
    private void applyUpdate(IRule rule, ETypeOfOperation op,
                             Optional<IRule> otherRule, IRuleSet target,
                             Boolean resetClone) {
        rwLock.writeLock().lock();
        try {
            switch (op) {
                case ADD -> {
                    if (target.getRules().contains(rule)) {
                        log.error("The rule you are trying to add already belongs to the current RuleSet");
                    } else {
                        target.addRule(rule);
                        if (resetClone) clonedRuleSetUnderTest = null;
                    }
                }
                case REMOVE -> {
                    if (!target.getRules().contains(rule)) {
                        log.error("The rule you are trying to delete does not belong to the current RuleSet");
                    } else {
                        target.removeRule(rule);
                        if (resetClone) clonedRuleSetUnderTest = null;
                    }
                }
                case UPDATE -> {
                    if (!target.getRules().contains(rule) ||
                        !(rule instanceof Rule old) ||
                          otherRule.isEmpty()||
                        !(otherRule.get() instanceof Rule updated)) {
                        log.error("Invalid rule or update target");
                    } else {
                        old.setDescription(updated.getDescription());
                        old.setDirection(updated.getDirection());
                        old.setSourceIPRange(updated.getSourceIPRange());
                        old.setDestinationIPRange(updated.getDestinationIPRange());
                        old.setSourcePortRange(updated.getSourcePortRange());
                        old.setDestinationPortRange(updated.getDestinationPortRange());
                        old.setProtocol(updated.getProtocol());
                        if (resetClone) clonedRuleSetUnderTest = null;
                    }
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    @LogPacket
    @Override
    public Boolean activeRuleSetProcessPacket(IPacket packet) {
        rwLock.readLock().lock();
        try {
            Boolean allowed = activeRuleSet.matches(packet);
            return allowed;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    @LogPacket
    @Override
    public Boolean clonedRuleSetUnderTestProcessPacket(IPacket packet) {
        rwLock.readLock().lock();
        try {
            if (this.clonedRuleSetUnderTest == null) {
                this.clonedRuleSetUnderTest = (IRuleSet) this.activeRuleSet.clone();
            }
            Boolean allowed = this.clonedRuleSetUnderTest.matches(packet);
            return allowed;
        } finally {
            rwLock.readLock().unlock();
        }
    }

}
