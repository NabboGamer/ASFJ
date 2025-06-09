package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.IRuleSet;
import it.unibas.softwarefirewall.firewallapi.ETypeOfOperation;
import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;


public class FirewallSimulator implements IFirewallFacade {
    
    private IRuleSet activeRuleSet;
    private IRuleSet clonedRuleSetUnderTest = null;

    // Devo sincronizzare l'accesso in lettura al set di regole(credo) perchè 
    // anche la vista da questo riferimento dovrebbe non solo mostrare a schermo
    // ma anche fare poi delle modifiche delle regole e queste modifiche potrebbero
    // avvenire concorrentemente all'accesso al set di regole da parte dei client 
    // tramite il metodo inspect() il quale per valutare se il pacchetto può
    // passare utilizza il set di regole ovviamente.
    @Override
    public IRuleSet getActiveRuleSet(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // Questo metodo non credo debba essere sincronizzato in fondo viene 
    // utilizzato solo nella tab secondaria di test del ruleset clonato.
    @Override
    public IRuleSet getClonedRuleSetUnderTest(){
        if (this.clonedRuleSetUnderTest == null) {
            this.clonedRuleSetUnderTest = (IRuleSet) this.activeRuleSet.clone();
        }
        return this.clonedRuleSetUnderTest;
    }
    
    // Stesso discorso fatto per getActiveRuleSet a maggior ragione valido qui, 
    // per eseguire delle operazioni sul ruleset devo sincronizzarne l'accesso.
    // Ho creato l'enum TypeOfOperation così so cosa ha scelto di fare l'utente lato 
    // GUI, in questo modo mi manda solo la regola e grazie al suo ID che è inmodificabile
    // posso cercarla nel ruleset ed eliminarla o modificarla, se si tratta di una aggiunta è easy.
    @Override
    public void updateActiveRuleSet(IRule rule, ETypeOfOperation typeOfOperation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // Anche qui come per getClonedRuleSetUnderTest() non credo debba essere sincronizzato.
    // Qui effettivamente avvengono modifiche ma ci accede solo l'utente tramite la GUI
    // non c'è pericolo di race condition con i client credo.
    @Override
    public void updateClonedRuleSetUnderTest(IRule rule, ETypeOfOperation typeOfOperation){
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
