package it.unibas.softwarefirewall.firewallgui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import it.unibas.softwarefirewall.firewallapi.IFirewallFacade;
import it.unibas.softwarefirewall.firewallapi.IPacketLogEntry;
import it.unibas.softwarefirewall.firewallapi.IPacketLogger;
import it.unibas.softwarefirewall.firewallapi.IPacketLoggerListener;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallapi.ISimulationStatusListener;
import it.unibas.softwarefirewall.firewallgui.controller.MainPanelController;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Singleton
public class MainPanel extends JPanel implements ISimulationStatusListener, IPacketLoggerListener {
    
    private Integer focusedTabIndex = 0;
    private String focusedTabTitle = "Firewall Live";
    private final RulesDetailsTableModel rulesDetailsTableModel;
    private final MainPanelController mainPanelController;
    private final IFirewallFacade firewall;
    private final IPacketLogger packetLogger;
    private final PacketsDetailsTableModel packetsDetailsTableModel;
    private final RulesDetailsTableModel rulesDetailsTableModelClonedRuleSet;
    
    @Inject
    public MainPanel(@Named("active") RulesDetailsTableModel rulesDetailsTableModel, MainPanelController mainPanelController, 
                     IFirewallFacade firewall, IPacketLogger packetLogger, PacketsDetailsTableModel packetsDetailsTableModel,
                     @Named("cloned") RulesDetailsTableModel rulesDetailsTableModelClonedRuleSet){
        this.rulesDetailsTableModel = rulesDetailsTableModel;
        this.mainPanelController = mainPanelController;
        this.firewall = firewall;
        this.packetLogger = packetLogger;
        this.packetsDetailsTableModel = packetsDetailsTableModel;
        this.rulesDetailsTableModelClonedRuleSet = rulesDetailsTableModelClonedRuleSet;
    }

    public void init() {
        initComponents();
        
        
        /////////////////////////////////////////Configure First Tab//////////////////////////////////////////
        this.mainPanelTabbedPane.addTab(null, this.firstTabPanel);
        // Create custom tab with text and icon on the right
        JLabel tabLabel1 = new JLabel("Firewall Live");
        FlatSVGIcon redDotSVG = new FlatSVGIcon("images/red_dot.svg", 14, 14);
        tabLabel1.setIcon(redDotSVG);
        tabLabel1.setIconTextGap(5);
        tabLabel1.setHorizontalTextPosition(SwingConstants.LEFT); // Text on the left, icon on the right
        this.mainPanelTabbedPane.setTabComponentAt(0, tabLabel1);
        this.mainPanelTabbedPane.setTitleAt(0, "Firewall Live");
        this.rulesDetailsTableModel.setRules(this.firewall.getActiveRuleSetRules());
        this.rulesDetailsTable.setModel(this.rulesDetailsTableModel);
        this.packetsDetailsTableModel.setPacketLogEntries(new ArrayList<>(this.packetLogger.getSnapshot()));
        this.filteredPacketsTable.setModel(this.packetsDetailsTableModel);
        this.filteredPacketsTable.setRowHeight(40);
        this.filteredPacketsTable.setDefaultRenderer(Boolean.class, new EmojiBooleanRenderer());
        this.filteredPacketsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Every time the panel (and therefore the table) is resized,
                // recalculate the column widths.
                resizeColumns(filteredPacketsTable);
            }
        });
        this.packetLogger.addPacketLoggerListener(this);
        this.mainPanelController.setRulesDetailsTable(this.rulesDetailsTable);
        this.addRuleButton.setAction(this.mainPanelController.getAddRuleAction());
        this.editRuleButton.setAction(this.mainPanelController.getEditRuleAction());
        this.removeRuleButton.setAction(this.mainPanelController.getRemoveRuleAction());
        this.startSimulationButton.setAction(this.mainPanelController.getStartSimulationAction());
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        
        
        /////////////////////////////////////////Configure Second Tab/////////////////////////////////////////
        this.mainPanelTabbedPane.addTab(null, this.secondTabPanel);
        JLabel tabLabel2 = new JLabel("Firewall Test");
        FlatSVGIcon hammerSVG = new FlatSVGIcon("images/hammer.svg", 18, 18);
        tabLabel2.setIcon(hammerSVG);
        tabLabel2.setIconTextGap(5);
        tabLabel2.setHorizontalTextPosition(SwingConstants.LEFT);
        this.mainPanelTabbedPane.setTabComponentAt(1, tabLabel2);
        this.mainPanelTabbedPane.setTitleAt(1, "Firewall Test");
        this.rulesDetailsTableModelClonedRuleSet.setRules(this.firewall.getClonedRuleSetUnderTestRules());
        this.rulesDetailsTableClonedRuleSet.setModel(this.rulesDetailsTableModelClonedRuleSet);
        this.mainPanelController.setRulesDetailsTableClonedRuleSet(this.rulesDetailsTableClonedRuleSet);
        this.addRuleButtonClonedRuleSet.setAction(this.mainPanelController.getAddRuleClonedRuleSetAction());
        this.editRuleButtonClonedRuleSet.setAction(this.mainPanelController.getEditRuleClonedRuleSetAction());
        this.removeRuleButtonClonedRuleSet.setAction(this.mainPanelController.getRemoveRuleClonedRuleSetAction());
        this.sendTestPacketButton.setAction(this.mainPanelController.getSendTestPacketAction());
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        
        
        this.mainPanelTabbedPane.addChangeListener(e -> {
            this.focusedTabIndex = mainPanelTabbedPane.getSelectedIndex();
            if(focusedTabIndex == 1){
                updateRulesDetailsTableClonedRuleSet();
            }
            //this.focusedTabTitle = mainPanelTabbedPane.getTitleAt(this.focusedTabIndex);
            //log.debug("New tab selected: " + this.focusedTabTitle + "." + " New tab index: " + focusedTabIndex);
        });
    }
    
    private void resizeColumns(JTable table) {
        int totalWidth = table.getParent().getWidth();

        // Desired proportions (15%, 5%, 80%)
        float[] proportions = {0.15f, 0.05f, 0.80f};

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            
            // Calculate width in pixel
            int newWidth = (int) (totalWidth * proportions[i]);
            
            // Set the preferred width of the column.
            // setPreferredWidth is the correct method to use in this context.
            column.setPreferredWidth(newWidth);
        }
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
    
    public void updateRulesDetailsTableClonedRuleSet(){
        List<IRule> clonedRuleSetRules = firewall.getClonedRuleSetUnderTestRules();
        this.rulesDetailsTableModelClonedRuleSet.setRules(clonedRuleSetRules);
        this.rulesDetailsTableModelClonedRuleSet.updateContent();
    }
    
    @Override
    public void onSimulationStarted() {
        SwingUtilities.invokeLater(() -> this.startSimulationButton.setEnabled(false));
    }

    @Override
    public void onSimulationFinished() {
        SwingUtilities.invokeLater(() -> this.startSimulationButton.setEnabled(true));
    }
    
    @Override
    public void onNewEntry() {
        SwingUtilities.invokeLater(() -> this.updateFilteredPacketsTable());
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
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
        rulesDetailsTableClonedRuleSet = new javax.swing.JTable();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        removeRuleButtonClonedRuleSet = new javax.swing.JButton();
        editRuleButtonClonedRuleSet = new javax.swing.JButton();
        addRuleButtonClonedRuleSet = new javax.swing.JButton();
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        octet1SourceIPTextField = new javax.swing.JTextField();
        octet2SourceIPTextField = new javax.swing.JTextField();
        octet3SourceIPTextField = new javax.swing.JTextField();
        octet4SourceIPTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        octet4DestinationIPTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        octet3DestinationIPTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        octet2DestinationIPTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        octet1DestinationIPTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel12 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel13 = new javax.swing.JLabel();
        sourcePortTextField = new javax.swing.JTextField();
        destinationPortTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel14 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel17 = new javax.swing.JLabel();
        protocolComboBox = new javax.swing.JComboBox<>();
        sendTestPacketButton = new javax.swing.JButton();
        javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
        payloadTextArea = new javax.swing.JTextArea();
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

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Test RuleSet", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("JetBrains Mono", 0, 12))); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(1228, 335));

        rulesDetailsTableClonedRuleSet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(rulesDetailsTableClonedRuleSet);

        jLabel2.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        jLabel2.setText(" Rules details");

        removeRuleButtonClonedRuleSet.setBackground(new java.awt.Color(255, 0, 0));
        removeRuleButtonClonedRuleSet.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        removeRuleButtonClonedRuleSet.setForeground(new java.awt.Color(255, 255, 255));
        removeRuleButtonClonedRuleSet.setText("- Remove");

        editRuleButtonClonedRuleSet.setBackground(new java.awt.Color(255, 153, 0));
        editRuleButtonClonedRuleSet.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        editRuleButtonClonedRuleSet.setForeground(new java.awt.Color(255, 255, 255));
        editRuleButtonClonedRuleSet.setText("~ Edit");

        addRuleButtonClonedRuleSet.setBackground(new java.awt.Color(0, 204, 0));
        addRuleButtonClonedRuleSet.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        addRuleButtonClonedRuleSet.setForeground(new java.awt.Color(255, 255, 255));
        addRuleButtonClonedRuleSet.setText("+ Add");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1166, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addRuleButtonClonedRuleSet, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(editRuleButtonClonedRuleSet, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(removeRuleButtonClonedRuleSet, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(removeRuleButtonClonedRuleSet)
                        .addComponent(editRuleButtonClonedRuleSet)
                        .addComponent(addRuleButtonClonedRuleSet)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Test Packet Editor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("JetBrains Mono", 0, 12))); // NOI18N
        jPanel4.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel4.setPreferredSize(new java.awt.Dimension(1228, 335));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Header", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("JetBrains Mono", 0, 12))); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(580, 273));

        jLabel4.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel4.setText("Source IP:");

        octet1SourceIPTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet1SourceIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet1SourceIPTextField.setToolTipText("0");

        octet2SourceIPTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet2SourceIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet2SourceIPTextField.setToolTipText("0");

        octet3SourceIPTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet3SourceIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet3SourceIPTextField.setToolTipText("0");

        octet4SourceIPTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet4SourceIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet4SourceIPTextField.setToolTipText("0");

        jLabel5.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(".");

        jLabel6.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText(".");

        jLabel7.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText(".");

        octet4DestinationIPTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet4DestinationIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet4DestinationIPTextField.setToolTipText("0");

        jLabel10.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText(".");

        octet3DestinationIPTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet3DestinationIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet3DestinationIPTextField.setToolTipText("0");

        jLabel9.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText(".");

        octet2DestinationIPTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet2DestinationIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet2DestinationIPTextField.setToolTipText("0");

        jLabel8.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText(".");

        octet1DestinationIPTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet1DestinationIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet1DestinationIPTextField.setToolTipText("0");

        jLabel12.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel12.setText("Destination IP:");

        jLabel13.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel13.setText("Source Port:");

        sourcePortTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        sourcePortTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sourcePortTextField.setToolTipText("0");

        destinationPortTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        destinationPortTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        destinationPortTextField.setToolTipText("0");

        jLabel14.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel14.setText("Destination Port:");

        jLabel17.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel17.setText("Protocol:");

        protocolComboBox.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        protocolComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TCP", "UDP", "ICMP" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(protocolComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sourcePortTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                    .addComponent(destinationPortTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(octet1DestinationIPTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(octet2DestinationIPTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(octet3DestinationIPTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(octet4DestinationIPTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(octet1SourceIPTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(octet2SourceIPTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(octet3SourceIPTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(octet4SourceIPTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(octet1SourceIPTextField)
                    .addComponent(jLabel5)
                    .addComponent(octet2SourceIPTextField)
                    .addComponent(jLabel6)
                    .addComponent(octet3SourceIPTextField)
                    .addComponent(jLabel7)
                    .addComponent(octet4SourceIPTextField)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(octet1DestinationIPTextField)
                    .addComponent(jLabel8)
                    .addComponent(octet2DestinationIPTextField)
                    .addComponent(jLabel9)
                    .addComponent(octet3DestinationIPTextField)
                    .addComponent(jLabel10)
                    .addComponent(octet4DestinationIPTextField)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sourcePortTextField))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(destinationPortTextField))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(protocolComboBox))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        sendTestPacketButton.setBackground(new java.awt.Color(28, 39, 76));
        sendTestPacketButton.setFont(new java.awt.Font("JetBrains Mono", 1, 18)); // NOI18N
        sendTestPacketButton.setForeground(new java.awt.Color(255, 255, 255));
        sendTestPacketButton.setText("Send Test Packet →");

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Payload", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("JetBrains Mono", 0, 12))); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(580, 273));

        payloadTextArea.setColumns(20);
        payloadTextArea.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        payloadTextArea.setRows(5);
        jScrollPane4.setViewportView(payloadTextArea);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sendTestPacketButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sendTestPacketButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout secondTabPanelLayout = new javax.swing.GroupLayout(secondTabPanel);
        secondTabPanel.setLayout(secondTabPanelLayout);
        secondTabPanelLayout.setHorizontalGroup(
            secondTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secondTabPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(secondTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 1188, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1188, Short.MAX_VALUE))
                .addContainerGap())
        );
        secondTabPanelLayout.setVerticalGroup(
            secondTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secondTabPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addContainerGap())
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
    private javax.swing.JButton addRuleButtonClonedRuleSet;
    private javax.swing.JTextField destinationPortTextField;
    private javax.swing.JButton editRuleButton;
    private javax.swing.JButton editRuleButtonClonedRuleSet;
    private javax.swing.JTable filteredPacketsTable;
    private javax.swing.JPanel firstTabPanel;
    private javax.swing.JTabbedPane mainPanelTabbedPane;
    private javax.swing.JTextField octet1DestinationIPTextField;
    private javax.swing.JTextField octet1SourceIPTextField;
    private javax.swing.JTextField octet2DestinationIPTextField;
    private javax.swing.JTextField octet2SourceIPTextField;
    private javax.swing.JTextField octet3DestinationIPTextField;
    private javax.swing.JTextField octet3SourceIPTextField;
    private javax.swing.JTextField octet4DestinationIPTextField;
    private javax.swing.JTextField octet4SourceIPTextField;
    private javax.swing.JTextArea payloadTextArea;
    private javax.swing.JComboBox<String> protocolComboBox;
    private javax.swing.JButton removeRuleButton;
    private javax.swing.JButton removeRuleButtonClonedRuleSet;
    private javax.swing.JTable rulesDetailsTable;
    private javax.swing.JTable rulesDetailsTableClonedRuleSet;
    private javax.swing.JPanel secondTabPanel;
    private javax.swing.JButton sendTestPacketButton;
    private javax.swing.JTextField sourcePortTextField;
    private javax.swing.JButton startSimulationButton;
    // End of variables declaration//GEN-END:variables

}
