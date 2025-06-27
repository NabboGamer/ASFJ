package it.unibas.softwarefirewall.firewallgui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallgui.controller.MainViewController;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class MainView extends JFrame {
    
    static {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }
    
    private final MainViewController mainViewController;
    private final MainPanel mainPanel;
    
    @Inject
    public MainView(MainViewController mainViewController, MainPanel mainPanel) {
        this.mainViewController = mainViewController;
        this.mainPanel = mainPanel;
    }

    public void init() {
        initComponents();
        this.setContentPane(new JScrollPane(this.mainPanel));
        this.menuItemExit.setAction(this.mainViewController.getExitAction());
        FlatSVGIcon shieldSVG = new FlatSVGIcon("images/shield.svg");
        this.setIconImage(shieldSVG.getImage());
        this.pack();
        this.setSize(1920, 1080);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    public void showInformationMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showWarningMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JMenuBar jMenuBar1 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu1 = new javax.swing.JMenu();
        menuItemExit = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ASFJ");
        setAlwaysOnTop(true);

        jMenu1.setText("File");

        menuItemExit.setText("Exit");
        jMenu1.add(menuItemExit);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1280, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 697, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem menuItemExit;
    // End of variables declaration//GEN-END:variables
}
