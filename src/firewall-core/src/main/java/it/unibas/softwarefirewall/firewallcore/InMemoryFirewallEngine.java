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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
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
        if (this.clonedRuleSetUnderTest == null) {
            this.clonedRuleSetUnderTest = (IRuleSet) this.activeRuleSet.clone();
        }
        return this.clonedRuleSetUnderTest;
    }
    
    @Override
    public void updateActiveRuleSet(IRule rule, ETypeOfOperation typeOfOperation) {
        this.rwLock.writeLock().lock();
        try {
            switch (typeOfOperation) {
                case ADD -> this.activeRuleSet.addRule(rule);
                case REMOVE -> this.activeRuleSet.removeRule(rule);
                case UPDATE -> {
                    this.activeRuleSet.removeRule(rule);
                    this.activeRuleSet.addRule(rule);
                }
            }
            this.clonedRuleSetUnderTest = null;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    @Override
    public void updateClonedRuleSetUnderTest(IRule rule, ETypeOfOperation typeOfOperation){
        switch (typeOfOperation) {
            case ADD -> this.clonedRuleSetUnderTest.addRule(rule);
            case REMOVE -> this.clonedRuleSetUnderTest.removeRule(rule);
            case UPDATE -> {
                this.clonedRuleSetUnderTest.removeRule(rule);
                this.clonedRuleSetUnderTest.addRule(rule);
            }
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
