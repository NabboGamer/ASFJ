package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.IRuleSet;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j
public class RuleSet implements IRuleSet, Cloneable {
    
    private List<IRule> rules = new ArrayList<>();

    @Override
    public void addRule(IRule newRule) {
        this.rules.add(newRule);
    }

    @Override
    public void removeRule(IRule rule) {
        this.rules.remove(rule);
    }
    
    public void loadRuleSetFromJson(String jsonPath){
        throw new UnsupportedOperationException();
    }

    // DENY ALL unless explicitly allowed by at least one rule
    @Override
    public Boolean matches(IPacket packet) {
        for (IRule rule : this.rules) {
            if(rule.matches(packet)){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object clone() {
        try {
            // 1. Eseguo la clonazione superficiale della classe base
            RuleSet clonedRuleSet = (RuleSet)super.clone();

            // 2. Creo una nuova lista per il clone
            clonedRuleSet.setRules(new ArrayList<>());

            // 3. Clono ogni regola e la aggiunge alla nuova lista
            for (IRule rule : this.rules) {
                clonedRuleSet.addRule((Rule)rule.clone());
            }
            return clonedRuleSet;
        } catch (CloneNotSupportedException cnse) {
            log.error("Error: not clonable object: {}", cnse);
            return null;
        }
    }
}
