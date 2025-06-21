package it.unibas.softwarefirewall.firewallgui.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;
import it.unibas.softwarefirewall.firewallgui.view.RuleFormDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
    
    private final Action saveEditedRuleAction = new SaveEditedRuleAction();
    private final Action saveAddedRuleAction = new SaveAddedRuleAction();
    private final Action cancelAction = new CancelAction();
    private final IFirewallFacade firewall;
    // Here I can't use a provider because the scope of RuleFormDialog is proptotype, 
    // so the provider would return me a new instance at each get
    private RuleFormDialog ruleFormDialog;

    @Inject
    public RuleFormDialogController(IFirewallFacade firewall) {
        this.firewall = firewall;
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
            
        }
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

}
