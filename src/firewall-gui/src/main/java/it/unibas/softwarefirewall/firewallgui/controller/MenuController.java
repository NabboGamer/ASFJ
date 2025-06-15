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
public class MenuController {
	
    private final Action exitAction = new ExitAction();

    public class ExitAction extends AbstractAction {

        public ExitAction() {
            this.putValue(Action.NAME, "Exit");
            this.putValue(Action.SHORT_DESCRIPTION, "Exit the application");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl E"));
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

}
