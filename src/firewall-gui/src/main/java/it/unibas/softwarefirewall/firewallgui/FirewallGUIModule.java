package it.unibas.softwarefirewall.firewallgui;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import it.unibas.softwarefirewall.firewallgui.view.RulesDetailsTableModel;
import lombok.extern.slf4j.Slf4j;
    
@Slf4j
public class FirewallGUIModule extends AbstractModule {
    
    @Override
    protected void configure() {
        // Needed to get two RulesDetailsTableModel Singletons,
        // if the injection is not qualified in this way then Guice being
        // the scope of RulesDetailsTableModel Singleton would always return the same
        // object when I try to inject it into two separate properties.
        bind(RulesDetailsTableModel.class)
            .annotatedWith(Names.named("active"))
            .to(RulesDetailsTableModel.class)
            .in(Singleton.class);

        bind(RulesDetailsTableModel.class)
            .annotatedWith(Names.named("cloned"))
            .to(RulesDetailsTableModel.class)
            .in(Singleton.class);

    }
    
}
