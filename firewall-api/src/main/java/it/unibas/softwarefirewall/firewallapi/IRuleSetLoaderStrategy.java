package it.unibas.softwarefirewall.firewallapi;

import java.io.IOException;
import java.util.List;

public interface IRuleSetLoaderStrategy {
    List<IRule> load(String source) throws IOException;
}