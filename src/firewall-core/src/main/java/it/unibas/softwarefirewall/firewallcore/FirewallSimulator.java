package it.unibas.softwarefirewall.firewallcore;

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


public class FirewallSimulator implements IFirewallFacade {
    
    private IRuleSet activeRuleSet;
    private IRuleSet clonedRuleSetUnderTest = null;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

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

    // Infine il metodo inspect(devo anche valutare il nome non mi piace molto e non credo sia il più appropriato)
    // ovviamente questo deve essere sincornizzato perchè per valutare se far passare o no
    // un pacchetto comunque deve accedere al RuleSet e chiamare matches.
    // Inoltre qui deve esserci il joining point dell'Aspect che si occuperà poi
    // di loggare il tutto, per loggare si intende aggiungere grazie al PacketLogger
    // una nuova entri nella coda di tipo PacketLog, questo è necessario per la tabella
    // di visualizzazione dei pacchetti nella GUI, credo convenga anche un log classico
    // a console poichè serve per il testing dei client.
    // Devo poi valutare anche se conviene utilizzare una coda thread-safe come ConcurrentLinkedQueue
    // poichè in fin dei conti il blocco penso debba essere tutto sincronizzato, ma questo
    // devo capire se influenza anche l'Aspect che di conseguenza è sincronizzato 
    // anche lui e anche il PacketLogger, se lo riguarda penso una Array list normale possa bastare,
    // se non lo riguarda è un bel problema serve sicuro una lista/coda thread-safe.
    @Override
    public Boolean inspect(IPacket packet) {
        rwLock.readLock().lock();
        try {
            Boolean allowed = activeRuleSet.matches(packet);
            // logging via Aspect...
            return allowed;
        } finally {
            rwLock.readLock().unlock();
        }
    }

}
