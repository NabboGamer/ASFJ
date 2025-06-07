package it.unibas.softwarefirewall.firewallcore;

import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.IRuleSetLoaderStrategy;
import java.io.IOException;
import java.util.List;

@Singleton
public class RuleSetLoaderXMLStrategy implements IRuleSetLoaderStrategy{
    
    @Override
    public List<IRule> load(String source) throws IOException {
        throw new UnsupportedOperationException("XML loader not yet implemented");
    }
}
