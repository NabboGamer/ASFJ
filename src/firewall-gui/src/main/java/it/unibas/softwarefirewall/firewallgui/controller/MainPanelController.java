package it.unibas.softwarefirewall.firewallgui.controller;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import it.unibas.softwarefirewall.clientsimulator.ClientSimulator;
import it.unibas.softwarefirewall.firewallapi.ETypeOfOperation;
import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallgui.view.MainPanel;
import it.unibas.softwarefirewall.firewallgui.view.MainView;
import it.unibas.softwarefirewall.firewallgui.view.RuleFormDialog;
import it.unibas.softwarefirewall.firewallgui.view.RulesDetailsTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Singleton
public class MainPanelController {
	
    private final Action addRuleAction = new AddRuleAction();
    private final Action editRuleAction = new EditRuleAction();
    private final Action removeRuleAction = new RemoveRuleAction();
    private final Action startSimulationAction = new StartSimulationAction();
    private final IFirewallFacade firewall;
    // This is to avoid cyclic dependencies, like MainPanel -> MainPanelController but MainPanelController -> MainPanel!
    // A Provider<T> is an interface provided by Guice that allows you to get an instance of T only when you need it, 
    // without it being injected immediately into the constructor. The Provider is therefore used to:
    // 1. Delay the initialization of an object;
    // 2. Break circular dependencies (A → B → A).
    private final Provider<MainPanel> mainPanelProvider;
    private final Provider<MainView> mainViewProvider;
    private final Provider<RuleFormDialog> ruleFormDialogProvider;
    private final Provider<ClientSimulator> clientSimulatorProvider;
    // The object is “generated” in the context of the view, so the view will 
    // take care of the set of this property
    private JTable rulesDetailsTable;
    
    @Inject
    public MainPanelController(IFirewallFacade firewall, Provider<MainPanel> mainPanelProvider, 
                               Provider<MainView> mainViewProvider, Provider<RuleFormDialog> ruleFormDialogProvider,
                               Provider<ClientSimulator> clientSimulatorProvider){
        this.firewall = firewall;
        this.mainPanelProvider = mainPanelProvider;
        this.mainViewProvider = mainViewProvider;
        this.ruleFormDialogProvider = ruleFormDialogProvider;
        this.clientSimulatorProvider = clientSimulatorProvider;
    }

    public class AddRuleAction extends AbstractAction {

        public AddRuleAction() {
            this.putValue(Action.NAME, "+ Add");
            this.putValue(Action.SHORT_DESCRIPTION, "Add a new rule to the firewall");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl A"));
        }

        public void actionPerformed(ActionEvent e) {
            RuleFormDialog ruleFormDialog = ruleFormDialogProvider.get();
            ruleFormDialog.showForAdd(Optional.empty());
        }
    }
    
    private class EditRuleAction extends AbstractAction {

        public EditRuleAction() {
            this.putValue(Action.NAME, "~ Edit");
            this.putValue(Action.SHORT_DESCRIPTION, "Edit an existing rule in the firewall");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl E"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IRule selectedRule = getSelectedRule();
            if(selectedRule == null){
                MainView mainView = mainViewProvider.get();
                mainView.showWarningMessage("Please select a rule from the table before continuing.");
                return;
            }
            RuleFormDialog ruleFormDialog = ruleFormDialogProvider.get();
            ruleFormDialog.showForEdit(Optional.of(selectedRule));
        }
    }
    
    private class RemoveRuleAction extends AbstractAction {

        public RemoveRuleAction() {
            this.putValue(Action.NAME, "- Remove");
            this.putValue(Action.SHORT_DESCRIPTION, "Remove an existing rule in the firewall");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl R"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IRule selectedRule = getSelectedRule();
            if(selectedRule == null){
                MainView mainView = mainViewProvider.get();
                mainView.showWarningMessage("Please select a rule from the table before continuing.");
                return;
            }
            firewall.updateActiveRuleSet(selectedRule, ETypeOfOperation.REMOVE, Optional.empty());
            MainPanel mainPanel = mainPanelProvider.get();
            mainPanel.updateRulesDetailsTable();
        }
    }
    
    private class StartSimulationAction extends AbstractAction {

        public StartSimulationAction() {
            this.putValue(Action.NAME, "Start Simulation ↓");
            this.putValue(Action.SHORT_DESCRIPTION, "Start simulating receiving packets from different clients");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl S"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ClientSimulator clientSimulator = clientSimulatorProvider.get();
            clientSimulator.addSimulationStatusListener(mainPanelProvider.get());
            clientSimulator.startSimulation();
        }
    }
    
    private IRule getSelectedRule(){
        int selectedRow = rulesDetailsTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        RulesDetailsTableModel rulesDetailsTableModel = (RulesDetailsTableModel) rulesDetailsTable.getModel();
        IRule rule = rulesDetailsTableModel.getRuleAt(selectedRow);
        return rule;
    }

}