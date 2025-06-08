package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.IRuleSet;
import it.unibas.softwarefirewall.firewallapi.IRuleSetLoaderStrategy;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class RuleSet implements IRuleSet, Cloneable {
    
    private List<IRule> rules = new ArrayList<>();
    private IRuleSetLoaderStrategy loader;
    
    public RuleSet(List<IRule> rules){
        this.rules = rules;
        IRuleSetLoaderStrategy ruleSetLoaderStrategy = RuleSetLoaderFactory.getInstance().getRuleSetLoaderStrategy();
        this.loader = ruleSetLoaderStrategy;
    }

    @Override
    public void addRule(IRule newRule) {
        this.rules.add(newRule);
    }

    @Override
    public void removeRule(IRule rule) {
        this.rules.remove(rule);
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
    
    @Override
    public void loadRuleSetFromFile() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("firewall-core.properties")) {
            props.load(in);
            String type = props.getProperty("firewallcore.ruleset.loader");
            String path = props.getProperty("firewallcore.ruleset." + type.toLowerCase() + ".path");
            List<IRule> loadedRules = loader.load(path);
            //rules.clear();
            this.rules.addAll(loadedRules);
            log.info("Loaded {} rules from {}", loadedRules.size(), path);
        } catch (Exception ex) {
            log.error("Error during RuleSet loading: ", ex);
        }
    }
}
