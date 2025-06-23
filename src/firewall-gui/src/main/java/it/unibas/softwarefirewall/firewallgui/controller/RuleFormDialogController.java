package it.unibas.softwarefirewall.firewallgui.controller;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallapi.EDirection;
import it.unibas.softwarefirewall.firewallapi.EProtocol;
import it.unibas.softwarefirewall.firewallapi.ETypeOfOperation;
import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;
import it.unibas.softwarefirewall.firewallapi.IRule;
////////////////////////////////////////////////////////////////////////////////
/// Note these are the only dependencies in the GUI that are related to the core.
/// The firewallcore module should be internal and there should be no other
/// modules directly dependent on it. A solution could be creating DTOs of these
/// 3 classes in the api and then introducing a mapper in IFirewallFacade.
import it.unibas.softwarefirewall.firewallcore.IPRange;
import it.unibas.softwarefirewall.firewallcore.PortRange;
import it.unibas.softwarefirewall.firewallcore.Rule;
////////////////////////////////////////////////////////////////////////////////
import it.unibas.softwarefirewall.firewallgui.view.MainPanel;
import it.unibas.softwarefirewall.firewallgui.view.MainView;
import it.unibas.softwarefirewall.firewallgui.view.RuleFormDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.StringJoiner;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Singleton
public class RuleFormDialogController {

    private final Action saveAddedRuleAction = new SaveAddedRuleAction();
    private final Action saveEditedRuleAction = new SaveEditedRuleAction();
    private final Action cancelAction = new CancelAction();
    private final Action saveAddedRuleClonedRuleSetAction = new SaveAddedRuleClonedRuleSetAction();
    private final Action saveEditedRuleClonedRuleSetAction = new SaveEditedRuleClonedRuleSetAction();
    private final IFirewallFacade firewall;
    private final Provider<MainPanel> mainPanelProvider;
    private final Provider<MainView> mainViewProvider;
    // Here I can't use a provider because the scope of RuleFormDialog is proptotype, 
    // so the provider would return me a new instance at each get
    private RuleFormDialog ruleFormDialog;

    @Inject
    public RuleFormDialogController(IFirewallFacade firewall, Provider<MainPanel> mainPanelProvider,
            Provider<MainView> mainViewProvider) {
        this.firewall = firewall;
        this.mainPanelProvider = mainPanelProvider;
        this.mainViewProvider = mainViewProvider;
    }
    
    private class SaveAddedRuleAction extends AbstractAction {

        public SaveAddedRuleAction() {
            this.putValue(Action.NAME, "Save");
            this.putValue(Action.SHORT_DESCRIPTION, "Save the rule just created");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl S"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IRule newRule = buildRuleFromFields();
            if(newRule == null){
                return;
            } else {
                firewall.updateActiveRuleSet(newRule, ETypeOfOperation.ADD, Optional.empty());
                MainPanel mainPanel = mainPanelProvider.get();
                mainPanel.updateRulesDetailsTable();
                ruleFormDialog.dispose();
            }     
        }
    }
    
    private class SaveAddedRuleClonedRuleSetAction extends AbstractAction {

        public SaveAddedRuleClonedRuleSetAction() {
            this.putValue(Action.NAME, "Save");
            this.putValue(Action.SHORT_DESCRIPTION, "Save the rule just created in the test rule set");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl S"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IRule newRule = buildRuleFromFields();
            if(newRule == null){
                return;
            } else {
                firewall.updateClonedRuleSetUnderTest(newRule, ETypeOfOperation.ADD, Optional.empty());
                MainPanel mainPanel = mainPanelProvider.get();
                mainPanel.updateRulesDetailsTableClonedRuleSet();
                ruleFormDialog.dispose();
            }     
        }
    }

    private class SaveEditedRuleAction extends AbstractAction {

        public SaveEditedRuleAction() {
            this.putValue(Action.NAME, "Save");
            this.putValue(Action.SHORT_DESCRIPTION, "Save changes made to the selected rule");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl S"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IRule selectedRule = ruleFormDialog.getRule().get();
            
            IRule newRule = buildRuleFromFields();
            if(newRule == null){
                return;
            } else {
                firewall.updateActiveRuleSet(selectedRule, ETypeOfOperation.UPDATE, Optional.of(newRule));
                MainPanel mainPanel = mainPanelProvider.get();
                mainPanel.updateRulesDetailsTable();
                ruleFormDialog.dispose();
            }    
        }
    }
    
    private class SaveEditedRuleClonedRuleSetAction extends AbstractAction {

        public SaveEditedRuleClonedRuleSetAction() {
            this.putValue(Action.NAME, "Save");
            this.putValue(Action.SHORT_DESCRIPTION, "Save changes made to the selected rule");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl S"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IRule selectedRule = ruleFormDialog.getRule().get();
            
            IRule newRule = buildRuleFromFields();
            if(newRule == null){
                return;
            } else {
                firewall.updateClonedRuleSetUnderTest(selectedRule, ETypeOfOperation.UPDATE, Optional.of(newRule));
                MainPanel mainPanel = mainPanelProvider.get();
                mainPanel.updateRulesDetailsTableClonedRuleSet();
                ruleFormDialog.dispose();
            }    
        }
    }

    private class CancelAction extends AbstractAction {

        public CancelAction() {
            this.putValue(Action.NAME, "Cancel");
            this.putValue(Action.SHORT_DESCRIPTION, "Cancels the current operation and returns to the previous window");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl C"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ruleFormDialog.dispose();
        }
    }

    private String validateFields(String descriptionString, String directionString, String octet1SourceIPRangeString,
                                  String octet2SourceIPRangeString, String octet3SourceIPRangeString,
                                  String octet4SourceIPRangeString, String netmaskSourceIPRangeString,
                                  String octet1DestinationIPRangeString, String octet2DestinationIPRangeString,
                                  String octet3DestinationIPRangeString, String octet4DestinationIPRangeString,
                                  String netmaskDestinationIPRangeString, String port1SourcePortRangeString,
                                  String port2SourcePortRangeString, String port1DestinationPortRangeString,
                                  String port2DestinationPortRangeString, String protocolString) {
        StringBuilder sb = new StringBuilder();
        if (descriptionString == null || descriptionString.isBlank()) {
            sb.append("The description is a mandatory field!").append("\n");
        }

        if (directionString == null || directionString.isBlank()) {
            sb.append("The direction is mandatory!").append("\n");
        } else {
            try {
                EDirection.valueOf(directionString);
            } catch (Exception e) {
                sb.append("Invalid Direction!").append("\n");
            }
        }

        if (octet1SourceIPRangeString == null || octet1SourceIPRangeString.isBlank()
                || octet2SourceIPRangeString == null || octet2SourceIPRangeString.isBlank()
                || octet3SourceIPRangeString == null || octet3SourceIPRangeString.isBlank()
                || octet4SourceIPRangeString == null || octet4SourceIPRangeString.isBlank()
                || netmaskSourceIPRangeString == null || netmaskSourceIPRangeString.isBlank()) {
            sb.append("All fields that make up the \"Source IP Range\" are mandatory!").append("\n");
        } else {
            IPRange sourceIPRange = validateIPRange(octet1SourceIPRangeString, octet2SourceIPRangeString,
                    octet3SourceIPRangeString, octet4SourceIPRangeString, netmaskSourceIPRangeString);
            if (sourceIPRange == null) {
                sb.append("Invalid \"Source IP Range\"!").append("\n");
            }
        }

        if (octet1DestinationIPRangeString == null || octet1DestinationIPRangeString.isBlank()
                || octet2DestinationIPRangeString == null || octet2DestinationIPRangeString.isBlank()
                || octet3DestinationIPRangeString == null || octet3DestinationIPRangeString.isBlank()
                || octet4DestinationIPRangeString == null || octet4DestinationIPRangeString.isBlank()
                || netmaskDestinationIPRangeString == null || netmaskDestinationIPRangeString.isBlank()) {
            sb.append("All fields that make up the \"Destination IP Range\" are mandatory!").append("\n");
        } else {
            IPRange destinationIPRange = validateIPRange(octet1DestinationIPRangeString, octet2DestinationIPRangeString,
                    octet3DestinationIPRangeString, octet4DestinationIPRangeString, netmaskDestinationIPRangeString);
            if (destinationIPRange == null) {
                sb.append("Invalid \"Destination IP Range\"!").append("\n");
            }
        }

        if (port1SourcePortRangeString == null || port1SourcePortRangeString.isBlank()
                || port2SourcePortRangeString == null || port2SourcePortRangeString.isBlank()) {
            sb.append("All fields that make up the \"Source Port Range\" are mandatory!").append("\n");
        } else {
            PortRange sourcePortRange = validatePortRange(port1SourcePortRangeString, port2SourcePortRangeString);
            if (sourcePortRange == null) {
                sb.append("Invalid \"Source Port Range\"!").append("\n");
            }
        }

        if (port1DestinationPortRangeString == null || port1DestinationPortRangeString.isBlank()
                || port2DestinationPortRangeString == null || port2DestinationPortRangeString.isBlank()) {
            sb.append("All fields that make up the \"Destination Port Range\" are mandatory!").append("\n");
        } else {
            PortRange destinationPortRange = validatePortRange(port1DestinationPortRangeString, port2DestinationPortRangeString);
            if (destinationPortRange == null) {
                sb.append("Invalid \"Destination Port Range\"!").append("\n");
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

        return sb.toString();
    }

    private IPRange validateIPRange(String octet1IPRangeString, String octet2IPRangeString,
            String octet3IPRangeString, String octet4IPRangeString, String netmaskIPRangeString) {
        try {
            Integer octet1SourceIPRange = Integer.valueOf(octet1IPRangeString);
            Integer octet2SourceIPRange = Integer.valueOf(octet2IPRangeString);
            Integer octet3SourceIPRange = Integer.valueOf(octet3IPRangeString);
            Integer octet4SourceIPRange = Integer.valueOf(octet4IPRangeString);
            Integer netmaskSourceIPRange = Integer.valueOf(netmaskIPRangeString);
            if (octet1SourceIPRange < 0 || octet1SourceIPRange > 255) {
                throw new IllegalArgumentException();
            }
            if (octet2SourceIPRange < 0 || octet2SourceIPRange > 255) {
                throw new IllegalArgumentException();
            }
            if (octet3SourceIPRange < 0 || octet3SourceIPRange > 255) {
                throw new IllegalArgumentException();
            }
            if (octet4SourceIPRange < 0 || octet4SourceIPRange > 255) {
                throw new IllegalArgumentException();
            }
            if (netmaskSourceIPRange < 0 || netmaskSourceIPRange > 32) {
                throw new IllegalArgumentException();
            }
            StringJoiner joiner = new StringJoiner(".");
            joiner.add(octet1IPRangeString.trim()).add(octet2IPRangeString.trim())
                    .add(octet3IPRangeString.trim()).add(octet4IPRangeString.trim());
            String sourceIPRangeString = joiner.toString() + "/" + netmaskIPRangeString.trim();
            IPRange ipRange = new IPRange(sourceIPRangeString);
            return ipRange;
        } catch (Exception e) {
            return null;
        }
    }

    private PortRange validatePortRange(String port1PortRangeString, String port2PortRangeString) {
        try {
            Integer port1PortRange = Integer.valueOf(port1PortRangeString);
            Integer port2PortRange = Integer.valueOf(port2PortRangeString);
            PortRange portRange = new PortRange(port1PortRange, port2PortRange);
            return portRange;
        } catch (Exception e) {
            return null;
        }
    }

    private IRule buildRuleFromFields() {
        String descriptionString = ruleFormDialog.getDescriptionTextField().getText();

        String directionString = ruleFormDialog.getDirectionComboBox().getSelectedItem().toString();

        String octet1SourceIPRangeString = ruleFormDialog.getOctet1SourceIPRange().getText();
        String octet2SourceIPRangeString = ruleFormDialog.getOctet2SourceIPRange().getText();
        String octet3SourceIPRangeString = ruleFormDialog.getOctet3SourceIPRange().getText();
        String octet4SourceIPRangeString = ruleFormDialog.getOctet4SourceIPRange().getText();
        String netmaskSourceIPRangeString = ruleFormDialog.getNetmaskSourceIPRange().getText();

        String octet1DestinationIPRangeString = ruleFormDialog.getOctet1DestinationIPRange().getText();
        String octet2DestinationIPRangeString = ruleFormDialog.getOctet2DestinationIPRange().getText();
        String octet3DestinationIPRangeString = ruleFormDialog.getOctet3DestinationIPRange().getText();
        String octet4DestinationIPRangeString = ruleFormDialog.getOctet4DestinationIPRange().getText();
        String netmaskDestinationIPRangeString = ruleFormDialog.getNetmaskDestinationIPRange().getText();

        String port1SourcePortRangeString = ruleFormDialog.getPort1SourcePortRange().getText();
        String port2SourcePortRangeString = ruleFormDialog.getPort2SourcePortRange().getText();

        String port1DestinationPortRangeString = ruleFormDialog.getPort1DestinationPortRange().getText();
        String port2DestinationPortRangeString = ruleFormDialog.getPort2DestinationPortRange().getText();

        String protocolString = ruleFormDialog.getProtocolComboBox().getSelectedItem().toString();

        String validateFieldsString = validateFields(descriptionString, directionString, octet1SourceIPRangeString,
                                                     octet2SourceIPRangeString, octet3SourceIPRangeString,
                                                     octet4SourceIPRangeString, netmaskSourceIPRangeString,
                                                     octet1DestinationIPRangeString, octet2DestinationIPRangeString,
                                                     octet3DestinationIPRangeString, octet4DestinationIPRangeString,
                                                     netmaskDestinationIPRangeString, port1SourcePortRangeString,
                                                     port2SourcePortRangeString, port1DestinationPortRangeString,
                                                     port2DestinationPortRangeString, protocolString);
        if (validateFieldsString.isBlank()) {
            EDirection direction = EDirection.valueOf(directionString);

            String sourceIPRangeString = octet1SourceIPRangeString + "."
                                       + octet2SourceIPRangeString + "."
                                       + octet3SourceIPRangeString + "."
                                       + octet4SourceIPRangeString + "/"
                                       + netmaskSourceIPRangeString;
            IPRange sourceIPRange = new IPRange(sourceIPRangeString);

            String destinationIPRangeString = octet1DestinationIPRangeString + "."
                                            + octet2DestinationIPRangeString + "."
                                            + octet3DestinationIPRangeString + "."
                                            + octet4DestinationIPRangeString + "/"
                                            + netmaskDestinationIPRangeString;
            IPRange destinationIPRange = new IPRange(destinationIPRangeString);

            PortRange sourcePortRange = new PortRange(Integer.valueOf(port1SourcePortRangeString),
                                                      Integer.valueOf(port2SourcePortRangeString));

            PortRange destinationPortRange = new PortRange(Integer.valueOf(port1DestinationPortRangeString),
                                                           Integer.valueOf(port2DestinationPortRangeString));

            EProtocol protocol = EProtocol.valueOf(protocolString);

            IRule newRule = new Rule(descriptionString, direction, sourceIPRange,
                                     destinationIPRange, sourcePortRange, 
                                     destinationPortRange, protocol);

            return newRule;
        } else {
            mainViewProvider.get().showErrorMessage(validateFieldsString);
            return null;
        }
    }
}
