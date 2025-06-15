package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import it.unibas.softwarefirewall.firewallapi.IRuleSet;
import it.unibas.softwarefirewall.firewallapi.IRuleSetLoaderStrategy;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
    
@Slf4j
public class FirewallCoreModule extends AbstractModule {
    
    @Override
    protected void configure() {
        this.bind(IFirewallFacade.class).to(InMemoryFirewallEngine.class);
        this.bind(IRuleSet.class).to(RuleSet.class);

        // Bind logging interceptor
        // Guice does not automatically create interceptors, nor does it bind them. You must:
        // 1) Instantiate or provide the interceptor;
        // 2) Inject its dependencies (if needed);
        // 3) Tell it where to apply via bindInterceptor().
        PacketLoggerInterceptor interceptor = new PacketLoggerInterceptor();
        this.requestInjection(interceptor);
        this.bindInterceptor(Matchers.any(), Matchers.annotatedWith(LogPacket.class), interceptor);
    }
    
    @Provides
    public IRuleSetLoaderStrategy provideRuleSetLoaderStrategy() {
        Properties props = new Properties();
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("firewall-core.properties")) {
            props.load(in);
            String loaderType = props.getProperty("firewallcore.ruleset.loader", "JSON");
            if ("XML".equalsIgnoreCase(loaderType)) {
                return new RuleSetLoaderXMLStrategy();
            }
        } catch (IOException e) {
            log.error("Could not load rule set loader configuration: {}", e.getMessage(), e);
        }
        return new RuleSetLoaderJsonStrategy(); // default fallback
    }
    
}
