package it.unibas.softwarefirewall.firewallgui.controller;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import it.unibas.softwarefirewall.clientsimulator.ClientSimulator;
import it.unibas.softwarefirewall.firewallapi.EProtocol;
import it.unibas.softwarefirewall.firewallapi.ETypeOfOperation;
import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;
import it.unibas.softwarefirewall.firewallapi.IHeader;
import it.unibas.softwarefirewall.firewallapi.IPacket;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallcore.PseudoHeader;
import it.unibas.softwarefirewall.firewallcore.PseudoPacket;
import it.unibas.softwarefirewall.firewallgui.ETypeOfRuleSet;
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
    private final Action addRuleClonedRuleSetAction = new AddRuleClonedRuleSetAction();
    private final Action editRuleClonedRuleSetAction = new EditRuleClonedRuleSetAction();
    private final Action removeRuleClonedRuleSetAction = new RemoveRuleClonedRuleSetAction();
    private final Action sendTestPacketAction = new SendTestPacketAction();
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
    private JTable rulesDetailsTableClonedRuleSet;

    @Inject
    public MainPanelController(IFirewallFacade firewall, Provider<MainPanel> mainPanelProvider,
            Provider<MainView> mainViewProvider, Provider<RuleFormDialog> ruleFormDialogProvider,
            Provider<ClientSimulator> clientSimulatorProvider) {
        this.firewall = firewall;
        this.mainPanelProvider = mainPanelProvider;
        this.mainViewProvider = mainViewProvider;
        this.ruleFormDialogProvider = ruleFormDialogProvider;
        this.clientSimulatorProvider = clientSimulatorProvider;
    }

    private class AddRuleAction extends AbstractAction {

        public AddRuleAction() {
            this.putValue(Action.NAME, "+ Add");
            this.putValue(Action.SHORT_DESCRIPTION, "Add a new rule to the firewall");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl A"));
        }

        public void actionPerformed(ActionEvent e) {
            RuleFormDialog ruleFormDialog = ruleFormDialogProvider.get();
            ruleFormDialog.showForAdd(Optional.empty(), ETypeOfRuleSet.ACTIVE);
        }
    }

    private class AddRuleClonedRuleSetAction extends AbstractAction {

        public AddRuleClonedRuleSetAction() {
            this.putValue(Action.NAME, "+ Add");
            this.putValue(Action.SHORT_DESCRIPTION, "Add a new rule to the test rule set");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl A"));
        }

        public void actionPerformed(ActionEvent e) {
            RuleFormDialog ruleFormDialog = ruleFormDialogProvider.get();
            ruleFormDialog.showForAdd(Optional.empty(), ETypeOfRuleSet.CLONED);
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
            if (selectedRule == null) {
                MainView mainView = mainViewProvider.get();
                mainView.showWarningMessage("Please select a rule from the table before continuing.");
                return;
            }
            RuleFormDialog ruleFormDialog = ruleFormDialogProvider.get();
            ruleFormDialog.showForEdit(Optional.of(selectedRule), ETypeOfRuleSet.ACTIVE);
        }
    }

    private class EditRuleClonedRuleSetAction extends AbstractAction {

        public EditRuleClonedRuleSetAction() {
            this.putValue(Action.NAME, "~ Edit");
            this.putValue(Action.SHORT_DESCRIPTION, "Edit an existing rule in the test rule set");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl E"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IRule selectedRule = getSelectedRuleClonedRuleSet();
            if (selectedRule == null) {
                MainView mainView = mainViewProvider.get();
                mainView.showWarningMessage("Please select a rule from the table before continuing.");
                return;
            }
            RuleFormDialog ruleFormDialog = ruleFormDialogProvider.get();
            ruleFormDialog.showForEdit(Optional.of(selectedRule), ETypeOfRuleSet.CLONED);
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
            if (selectedRule == null) {
                MainView mainView = mainViewProvider.get();
                mainView.showWarningMessage("Please select a rule from the table before continuing.");
                return;
            }
            firewall.updateActiveRuleSet(selectedRule, ETypeOfOperation.REMOVE, Optional.empty());
            MainPanel mainPanel = mainPanelProvider.get();
            mainPanel.updateRulesDetailsTable();
        }
    }

    private class RemoveRuleClonedRuleSetAction extends AbstractAction {

        public RemoveRuleClonedRuleSetAction() {
            this.putValue(Action.NAME, "- Remove");
            this.putValue(Action.SHORT_DESCRIPTION, "Remove an existing rule in the test rule set");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl R"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IRule selectedRule = getSelectedRuleClonedRuleSet();
            if (selectedRule == null) {
                MainView mainView = mainViewProvider.get();
                mainView.showWarningMessage("Please select a rule from the table before continuing.");
                return;
            }
            firewall.updateClonedRuleSetUnderTest(selectedRule, ETypeOfOperation.REMOVE, Optional.empty());
            MainPanel mainPanel = mainPanelProvider.get();
            mainPanel.updateRulesDetailsTableClonedRuleSet();
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

    private class SendTestPacketAction extends AbstractAction {

        public SendTestPacketAction() {
            this.putValue(Action.NAME, "Send Test Packet →");
            this.putValue(Action.SHORT_DESCRIPTION, "Send test packet to test rule set");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl S"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IPacket testPacket = buildPacketFromFields();
            if(testPacket == null){
                return;
            }
            Boolean allowed = firewall.clonedRuleSetUnderTestProcessPacket(testPacket);
//            log.debug("Allowed: " + allowed);
            if(allowed){
                mainViewProvider.get().showInformationMessage("Test Packet Allowed");
            } else {
                mainViewProvider.get().showWarningMessage("Test Packet NOT Allowed");
            }
        }
    }

    private IRule getSelectedRule() {
        int selectedRow = rulesDetailsTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        RulesDetailsTableModel rulesDetailsTableModel = (RulesDetailsTableModel) rulesDetailsTable.getModel();
        IRule rule = rulesDetailsTableModel.getRuleAt(selectedRow);
        return rule;
    }

    private IRule getSelectedRuleClonedRuleSet() {
        int selectedRow = rulesDetailsTableClonedRuleSet.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        RulesDetailsTableModel rulesDetailsTableModelClonedRuleSet = (RulesDetailsTableModel) rulesDetailsTableClonedRuleSet.getModel();
        IRule rule = rulesDetailsTableModelClonedRuleSet.getRuleAt(selectedRow);
        return rule;
    }

    private String validateFields(String octet1SourceIPString, String octet2SourceIPString,
            String octet3SourceIPString, String octet4SourceIPString,
            String octet1DestinationIPString, String octet2DestinationIPString,
            String octet3DestinationIPString, String octet4DestinationIPString,
            String sourcePortString, String destinationPortString,
            String protocolString, String payloadString) {
        StringBuilder sb = new StringBuilder();

        if (octet1SourceIPString == null || octet1SourceIPString.isBlank()
                || octet2SourceIPString == null || octet2SourceIPString.isBlank()
                || octet3SourceIPString == null || octet3SourceIPString.isBlank()
                || octet4SourceIPString == null || octet4SourceIPString.isBlank()) {
            sb.append("All fields that make up the \"Source IP\" are mandatory!").append("\n");
        } else {
            Boolean isSourceIPValid = validateIP(octet1SourceIPString, octet2SourceIPString,
                    octet3SourceIPString, octet4SourceIPString);
            if (isSourceIPValid == false) {
                sb.append("Invalid \"Source IP\"!").append("\n");
            }
        }

        if (octet1DestinationIPString == null || octet1DestinationIPString.isBlank()
                || octet2DestinationIPString == null || octet2DestinationIPString.isBlank()
                || octet3DestinationIPString == null || octet3DestinationIPString.isBlank()
                || octet4DestinationIPString == null || octet4DestinationIPString.isBlank()) {
            sb.append("All fields that make up the \"Destination IP\" are mandatory!").append("\n");
        } else {
            Boolean isDestinationIPValid = validateIP(octet1DestinationIPString, octet2DestinationIPString,
                    octet3DestinationIPString, octet4DestinationIPString);
            if (isDestinationIPValid == false) {
                sb.append("Invalid \"Destination IP\"!").append("\n");
            }
        }

        if (sourcePortString == null || sourcePortString.isBlank()) {
            sb.append("All fields that make up the \"Source Port\" are mandatory!").append("\n");
        } else {
            Boolean isSourcePortValid = validatePort(sourcePortString);
            if (isSourcePortValid == false) {
                sb.append("Invalid \"Source Port\"!").append("\n");
            }
        }

        if (destinationPortString == null || destinationPortString.isBlank()) {
            sb.append("All fields that make up the \"Destination Port\" are mandatory!").append("\n");
        } else {
            Boolean isDestinationPortValid = validatePort(destinationPortString);
            if (isDestinationPortValid == false) {
                sb.append("Invalid \"Destination Port\"!").append("\n");
            }
        }

        if (protocolString == null || protocolString.isBlank()) {
            sb.append("The protocol is mandatory!").append("\n");
        } else {
            try {
                EProtocol.valueOf(protocolString);
            } catch (Exception e) {
                sb.append("Invalid Protocol!").append("\n");
            }
        }
        
        if (payloadString == null || payloadString.isBlank()) {
            sb.append("The payload is a mandatory field!").append("\n");
        }

        return sb.toString();
    }

    private Boolean validateIP(String octet1IPString, String octet2IPString,
            String octet3IPString, String octet4IPString) {
        try {
            Integer octet1SourceIP = Integer.valueOf(octet1IPString);
            Integer octet2SourceIP = Integer.valueOf(octet2IPString);
            Integer octet3SourceIP = Integer.valueOf(octet3IPString);
            Integer octet4SourceIP = Integer.valueOf(octet4IPString);
            if (octet1SourceIP < 0 || octet1SourceIP > 255) {
                throw new IllegalArgumentException();
            }
            if (octet2SourceIP < 0 || octet2SourceIP > 255) {
                throw new IllegalArgumentException();
            }
            if (octet3SourceIP < 0 || octet3SourceIP > 255) {
                throw new IllegalArgumentException();
            }
            if (octet4SourceIP < 0 || octet4SourceIP > 255) {
                throw new IllegalArgumentException();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Boolean validatePort(String portString) {
        try {
            Integer port = Integer.valueOf(portString);
            if (port < 0 || port > 65535) {
                throw new IllegalArgumentException();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private IPacket buildPacketFromFields() {
        MainPanel mainPanel = mainPanelProvider.get();

        String octet1SourceIPString = mainPanel.getOctet1SourceIPTextField().getText();
        String octet2SourceIPString = mainPanel.getOctet2SourceIPTextField().getText();
        String octet3SourceIPString = mainPanel.getOctet3SourceIPTextField().getText();
        String octet4SourceIPString = mainPanel.getOctet4SourceIPTextField().getText();

        String octet1DestinationIPString = mainPanel.getOctet1DestinationIPTextField().getText();
        String octet2DestinationIPString = mainPanel.getOctet2DestinationIPTextField().getText();
        String octet3DestinationIPString = mainPanel.getOctet3DestinationIPTextField().getText();
        String octet4DestinationIPString = mainPanel.getOctet4DestinationIPTextField().getText();

        String sourcePortString = mainPanel.getSourcePortTextField().getText();

        String destinationPortString = mainPanel.getDestinationPortTextField().getText();

        String protocolString = mainPanel.getProtocolComboBox().getSelectedItem().toString();
        
        String payloadString = mainPanel.getPayloadTextArea().getText();

        String validateFieldsString = validateFields(octet1SourceIPString,octet2SourceIPString, 
                                                     octet3SourceIPString, octet4SourceIPString,
                                                     octet1DestinationIPString, octet2DestinationIPString,
                                                     octet3DestinationIPString, octet4DestinationIPString,
                                                     sourcePortString, destinationPortString, 
                                                     protocolString, payloadString);
        if (validateFieldsString.isBlank()) {

            String sourceIPString = octet1SourceIPString + "."
                    + octet2SourceIPString + "."
                    + octet3SourceIPString + "."
                    + octet4SourceIPString;

            String destinationIPString = octet1DestinationIPString + "."
                    + octet2DestinationIPString + "."
                    + octet3DestinationIPString + "."
                    + octet4DestinationIPString;

            Integer sourcePort = Integer.valueOf(sourcePortString);

            Integer destinationPort = Integer.valueOf(destinationPortString);

            EProtocol protocol = EProtocol.valueOf(protocolString);
            
            IHeader header = new PseudoHeader(sourceIPString, destinationIPString, sourcePort, destinationPort, protocol);
            
            IPacket packet = new PseudoPacket(header, payloadString);

            return packet;
        } else {
            mainViewProvider.get().showErrorMessage(validateFieldsString);
            return null;
        }
    }
}
