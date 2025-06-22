package it.unibas.softwarefirewall.firewallgui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;
import it.unibas.softwarefirewall.firewallapi.IPacketLogEntry;
import it.unibas.softwarefirewall.firewallapi.IPacketLogger;
import it.unibas.softwarefirewall.firewallapi.IPacketLoggerListener;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.ISimulationStatusListener;
import it.unibas.softwarefirewall.firewallgui.controller.MainPanelController;
import java.util.ArrayList;
import java.util.List;
import static javax.swing.GroupLayout.Alignment.CENTER;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class MainPanel extends JPanel implements ISimulationStatusListener, IPacketLoggerListener {
    
    private Integer focusedTabIndex = 0;
    private String focusedTabTitle = "Firewall Live";
    private final RulesDetailsTableModel rulesDetailsTableModel;
    private final MainPanelController mainPanelController;
    private final IFirewallFacade firewall;
    private final IPacketLogger packetLogger;
    private final PacketsDetailsTableModel packetsDetailsTableModel;
    
    @Inject
    public MainPanel(RulesDetailsTableModel rulesDetailsTableModel, MainPanelController mainPanelController, 
                     IFirewallFacade firewall, IPacketLogger packetLogger, PacketsDetailsTableModel packetsDetailsTableModel){
        this.rulesDetailsTableModel = rulesDetailsTableModel;
        this.mainPanelController = mainPanelController;
        this.firewall = firewall;
        this.packetLogger = packetLogger;
        this.packetsDetailsTableModel = packetsDetailsTableModel;
    }

    public void init() {
        initComponents();
        
        // Configure First Tab
        this.mainPanelTabbedPane.addTab(null, this.firstTabPanel);
        // Create custom tab with text and icon on the right
        JLabel tabLabel1 = new JLabel("Firewall Live");
        FlatSVGIcon redDotSVG = new FlatSVGIcon("images/red_dot.svg", 14, 14);
        tabLabel1.setIcon(redDotSVG);
        tabLabel1.setIconTextGap(5);
        tabLabel1.setHorizontalTextPosition(SwingConstants.LEFT); // Text on the left, icon on the right
        this.mainPanelTabbedPane.setTabComponentAt(0, tabLabel1);
        this.mainPanelTabbedPane.setTitleAt(0, "Firewall Live");
        
        // Configure Second Tab
        this.mainPanelTabbedPane.addTab(null, this.secondTabPanel);
        JLabel tabLabel2 = new JLabel("Firewall Test");
        FlatSVGIcon hammerSVG = new FlatSVGIcon("images/hammer.svg", 18, 18);
        tabLabel2.setIcon(hammerSVG);
        tabLabel2.setIconTextGap(5);
        tabLabel2.setHorizontalTextPosition(SwingConstants.LEFT);
        this.mainPanelTabbedPane.setTabComponentAt(1, tabLabel2);
        this.mainPanelTabbedPane.setTitleAt(1, "Firewall Test");
        
        this.mainPanelTabbedPane.addChangeListener(e -> {
            this.focusedTabIndex = mainPanelTabbedPane.getSelectedIndex();
            this.focusedTabTitle = mainPanelTabbedPane.getTitleAt(this.focusedTabIndex);
            log.debug("New tab selected: " + this.focusedTabTitle);
        });
        
        this.rulesDetailsTableModel.setRules(this.firewall.getActiveRuleSetRules());
        this.rulesDetailsTable.setModel(this.rulesDetailsTableModel);
        
        this.packetsDetailsTableModel.setPacketLogEntries(new ArrayList<>(this.packetLogger.getSnapshot()));
        this.filteredPacketsTable.setModel(this.packetsDetailsTableModel);
        
//        this.filteredPacketsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        int width = this.filteredPacketsTable.getWidth();
//        log.debug("Width: {}", width);
//        TableColumnModel cols = this.filteredPacketsTable.getColumnModel();  
//        cols.getColumn(0).setPreferredWidth(Double.valueOf(0.20*width).intValue());  // Arrival time
//        cols.getColumn(1).setPreferredWidth(Double.valueOf(0.10*width).intValue());  // Allowed
//        cols.getColumn(2).setPreferredWidth(Double.valueOf(0.70*width).intValue());  // Packet
        this.filteredPacketsTable.setRowHeight(40);
        this.filteredPacketsTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
              setText(value == null ? "" : value.toString());
            }
          });
        
        this.filteredPacketsTable.setDefaultRenderer(Icon.class, new DefaultTableCellRenderer() {
            @Override
            public void setHorizontalAlignment(int alignment) {
              super.setHorizontalAlignment(CENTER);
            }
        });
        
        this.packetLogger.addPacketLoggerListener(this);
        
        this.mainPanelController.setRulesDetailsTable(this.rulesDetailsTable);
        
        this.addRuleButton.setAction(this.mainPanelController.getAddRuleAction());
        this.editRuleButton.setAction(this.mainPanelController.getEditRuleAction());
        this.removeRuleButton.setAction(this.mainPanelController.getRemoveRuleAction());
        this.startSimulationButton.setAction(this.mainPanelController.getStartSimulationAction());
    }
    
    public void updateRulesDetailsTable(){
        List<IRule> activeRuleSetRules = firewall.getActiveRuleSetRules();
        this.rulesDetailsTableModel.setRules(activeRuleSetRules);
        this.rulesDetailsTableModel.updateContent();
    }
    
    public void updateFilteredPacketsTable() {
        List<IPacketLogEntry> packetLogEntries = new ArrayList<>(this.packetLogger.getSnapshot());
        this.packetsDetailsTableModel.setPacketLogEntries(packetLogEntries);
        this.packetsDetailsTableModel.updateContent();
    }
    
    @Override
    public void onSimulationStarted() {
        SwingUtilities.invokeLater(() -> startSimulationButton.setEnabled(false));
    }

    @Override
    public void onSimulationFinished() {
        SwingUtilities.invokeLater(() -> startSimulationButton.setEnabled(true));
    }
    
    @Override
    public void onNewEntry() {
        this.updateFilteredPacketsTable();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        firstTabPanel = new javax.swing.JPanel();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        rulesDetailsTable = new javax.swing.JTable();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        removeRuleButton = new javax.swing.JButton();
        editRuleButton = new javax.swing.JButton();
        addRuleButton = new javax.swing.JButton();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        filteredPacketsTable = new javax.swing.JTable();
        startSimulationButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        secondTabPanel = new javax.swing.JPanel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        mainPanelTabbedPane = new javax.swing.JTabbedPane();

        firstTabPanel.setName("firstTabPanel"); // NOI18N
        firstTabPanel.setPreferredSize(new java.awt.Dimension(1200, 680));
        firstTabPanel.setRequestFocusEnabled(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Active RuleSet", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("JetBrains Mono", 0, 12))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1228, 335));

        rulesDetailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(rulesDetailsTable);

        jLabel1.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        jLabel1.setText(" Rules details");

        removeRuleButton.setBackground(new java.awt.Color(255, 0, 0));
        removeRuleButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        removeRuleButton.setForeground(new java.awt.Color(255, 255, 255));
        removeRuleButton.setText("- Remove");

        editRuleButton.setBackground(new java.awt.Color(255, 153, 0));
        editRuleButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        editRuleButton.setForeground(new java.awt.Color(255, 255, 255));
        editRuleButton.setText("~ Edit");

        addRuleButton.setBackground(new java.awt.Color(0, 204, 0));
        addRuleButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        addRuleButton.setForeground(new java.awt.Color(255, 255, 255));
        addRuleButton.setText("+ Add");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1166, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addRuleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(editRuleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(removeRuleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(removeRuleButton)
                        .addComponent(editRuleButton)
                        .addComponent(addRuleButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtered Packets", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("JetBrains Mono", 0, 12))); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(1228, 335));

        filteredPacketsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(filteredPacketsTable);

        startSimulationButton.setBackground(new java.awt.Color(28, 39, 76));
        startSimulationButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        startSimulationButton.setForeground(new java.awt.Color(255, 255, 255));
        startSimulationButton.setText("Start Simulation ↓");

        jLabel3.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        jLabel3.setText(" Packets details");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1166, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(startSimulationButton)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startSimulationButton)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout firstTabPanelLayout = new javax.swing.GroupLayout(firstTabPanel);
        firstTabPanel.setLayout(firstTabPanelLayout);
        firstTabPanelLayout.setHorizontalGroup(
            firstTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(firstTabPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(firstTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1188, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1188, Short.MAX_VALUE))
                .addContainerGap())
        );
        firstTabPanelLayout.setVerticalGroup(
            firstTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(firstTabPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        secondTabPanel.setName("secondTabPanel"); // NOI18N

        jLabel2.setText("Test Label Seconda Tab");

        javax.swing.GroupLayout secondTabPanelLayout = new javax.swing.GroupLayout(secondTabPanel);
        secondTabPanel.setLayout(secondTabPanelLayout);
        secondTabPanelLayout.setHorizontalGroup(
            secondTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secondTabPanelLayout.createSequentialGroup()
                .addGap(150, 150, 150)
                .addComponent(jLabel2)
                .addContainerGap(1007, Short.MAX_VALUE))
        );
        secondTabPanelLayout.setVerticalGroup(
            secondTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secondTabPanelLayout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(jLabel2)
                .addContainerGap(611, Short.MAX_VALUE))
        );

        setPreferredSize(new java.awt.Dimension(1280, 720));

        mainPanelTabbedPane.setName("mainPanelTabbedPane"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanelTabbedPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanelTabbedPane)
                .addContainerGap())
        );

        mainPanelTabbedPane.getAccessibleContext().setAccessibleName("");
        mainPanelTabbedPane.getAccessibleContext().setAccessibleDescription("");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRuleButton;
    private javax.swing.JButton editRuleButton;
    private javax.swing.JTable filteredPacketsTable;
    private javax.swing.JPanel firstTabPanel;
    private javax.swing.JTabbedPane mainPanelTabbedPane;
    private javax.swing.JButton removeRuleButton;
    private javax.swing.JTable rulesDetailsTable;
    private javax.swing.JPanel secondTabPanel;
    private javax.swing.JButton startSimulationButton;
    // End of variables declaration//GEN-END:variables

}
