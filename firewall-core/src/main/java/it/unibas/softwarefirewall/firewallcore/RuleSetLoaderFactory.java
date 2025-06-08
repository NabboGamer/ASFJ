package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IRuleSetLoaderStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleSetLoaderFactory {

    private static final RuleSetLoaderFactory INSTANCE = new RuleSetLoaderFactory();
    private IRuleSetLoaderStrategy ruleSetLoaderStrategy;

    private RuleSetLoaderFactory() {
        init();
    }

    public static RuleSetLoaderFactory getInstance() {
        return INSTANCE;
    }

    private void init() {
        Properties props = new Properties();
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("firewall-core.properties")) {
            props.load(in);
            String loaderType = props.getProperty("firewallcore.ruleset.loader", "JSON");
            if ("XML".equalsIgnoreCase(loaderType)) {
                this.ruleSetLoaderStrategy = new RuleSetLoaderXMLStrategy();
                return;
            }
        } catch (IOException e) {
            log.error("Could not load rule set loader configuration: {}", e.getMessage(), e);
        }
        // Default fallback
        this.ruleSetLoaderStrategy = new RuleSetLoaderJsonStrategy();
    }

    public IRuleSetLoaderStrategy getRuleSetLoaderStrategy() {
        return ruleSetLoaderStrategy;
    }
}
