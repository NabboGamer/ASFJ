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
    public void updateActiveRuleSet(IRule rule, ETypeOfOperation typeOfOperation, Optional<IRule> otherRule) throws IllegalArgumentException {
        this.rwLock.writeLock().lock();
        try {
            switch (typeOfOperation) {
                case ADD -> {
                    if (this.activeRuleSet.getRules().contains(rule)) {
                        throw new IllegalArgumentException("The rule you are trying to add already belongs to the current RuleSet. Are you sure you didn't mean to update it?");
                    }
                    this.activeRuleSet.addRule(rule);
                }
                case REMOVE -> {
                    if (!this.activeRuleSet.getRules().contains(rule)) {
                        throw new IllegalArgumentException("The rule you are trying to delete does not belong to the current RuleSet");
                    }
                    this.activeRuleSet.removeRule(rule);
                }
                case UPDATE -> {
                    if (!this.activeRuleSet.getRules().contains(rule) || !(rule instanceof Rule old) || 
                        otherRule.isEmpty() || !(otherRule.get() instanceof Rule updated)) {
                        throw new IllegalArgumentException("Invalid rule or update target");
                    }
                    old.setDescription(updated.getDescription());
                    old.setDirection(updated.getDirection());
                    old.setSourceIPRange(updated.getSourceIPRange());
                    old.setDestinationIPRange(updated.getDestinationIPRange());
                    old.setSourcePortRange(updated.getSourcePortRange());
                    old.setDestinationPortRange(updated.getDestinationPortRange());
                    old.setProtocol(updated.getProtocol());
                }
            }
            this.clonedRuleSetUnderTest = null;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    @Override
    public void updateClonedRuleSetUnderTest(IRule rule, ETypeOfOperation typeOfOperation, Optional<IRule> otherRule) throws IllegalArgumentException {
        this.rwLock.writeLock().lock();
        try {
            switch (typeOfOperation) {
                case ADD -> {
                    if (this.clonedRuleSetUnderTest.getRules().contains(rule)) {
                        throw new IllegalArgumentException("The rule you are trying to add already belongs to the cloned RuleSet. Are you sure you didn't mean to update it?");
                    }
                    this.clonedRuleSetUnderTest.addRule(rule);
                }
                case REMOVE -> {
                    if (!this.clonedRuleSetUnderTest.getRules().contains(rule)) {
                        throw new IllegalArgumentException("The rule you are trying to delete does not belong to the cloned RuleSet");
                    }
                    this.clonedRuleSetUnderTest.removeRule(rule);
                }
                case UPDATE -> {
                    if (!this.clonedRuleSetUnderTest.getRules().contains(rule) || !(rule instanceof Rule old) || 
                        otherRule.isEmpty() || !(otherRule.get() instanceof Rule updated)) {
                        throw new IllegalArgumentException("Invalid rule or update target");
                    }
                    old.setDescription(updated.getDescription());
                    old.setDirection(updated.getDirection());
                    old.setSourceIPRange(updated.getSourceIPRange());
                    old.setDestinationIPRange(updated.getDestinationIPRange());
                    old.setSourcePortRange(updated.getSourcePortRange());
                    old.setDestinationPortRange(updated.getDestinationPortRange());
                    old.setProtocol(updated.getProtocol());
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    @LogPacket
    @Override
    public Boolean processPacket(IPacket packet) {
        rwLock.readLock().lock();
        try {
            Boolean allowed = activeRuleSet.matches(packet);
            return allowed;
        } finally {
            rwLock.readLock().unlock();
        }
    }

}
