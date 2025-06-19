package it.unibas.softwarefirewall.firewallgui.controller;

import com.google.inject.Singleton;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Singleton
public class MainPanelController {
	
    private final Action addRuleAction = new AddRuleAction();
    private final Action editRuleAction = new EditRuleAction();
    private final Action removeRuleAction = new RemoveRuleAction();

    public class AddRuleAction extends AbstractAction {

        public AddRuleAction() {
            this.putValue(Action.NAME, "+ Add");
            this.putValue(Action.SHORT_DESCRIPTION, "Add a new rule to the firewall");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl A"));
        }

        public void actionPerformed(ActionEvent e) {
            
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
            
        }
    }

}