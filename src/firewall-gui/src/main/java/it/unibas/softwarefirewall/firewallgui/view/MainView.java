package it.unibas.softwarefirewall.firewallgui.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallgui.controller.MenuController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class MainView extends javax.swing.JFrame {
    
    private final MenuController menuController;
    
    @Inject
    public MainView(MenuController menuController) {
        this.menuController = menuController;
    }

    public void init() {
        initComponents();
//        setContentPane(new JScrollPane(Applicazione.getInstance().getVistaPrincipale()));
        this.menuItemExit.setAction(this.menuController.getExitAction());
        this.pack();
        this.setSize(1920, 1080);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuItemExit = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem menuItemExit;
    // End of variables declaration//GEN-END:variables
}
