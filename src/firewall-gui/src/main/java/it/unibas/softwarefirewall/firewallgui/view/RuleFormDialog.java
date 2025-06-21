package it.unibas.softwarefirewall.firewallgui.view;

import com.google.inject.Inject;
import it.unibas.softwarefirewall.firewallapi.IRule;
import it.unibas.softwarefirewall.firewallgui.controller.RuleFormDialogController;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Optional;
import javax.swing.JDialog;
import javax.swing.JTextField;

// Note there is no Guice annotation so this implies 
// that the scope is the default one i.e. prototype
public class RuleFormDialog extends JDialog {
    
    private final MainView mainView;
    private final RuleFormDialogController ruleFormDialogController;
    private Optional<IRule> rule;

    @Inject
    public RuleFormDialog(MainView mainView, RuleFormDialogController ruleFormDialogController) {
        super(mainView, true);
        this.mainView = mainView;
        this.ruleFormDialogController = ruleFormDialogController;
        initComponents();
        this.ruleFormDialogController.setRuleFormDialog(this);
        this.cancelButton.setAction(this.ruleFormDialogController.getCancelAction());
    }

    public void showForAdd(Optional<IRule> rule) {
        this.rule = rule;
//        resetForm();
        this.saveButton.setAction(this.ruleFormDialogController.getSaveAddedRuleAction());
        this.setTitle("Edit selected rule");
        this.getContentPane().invalidate();
        this.getContentPane().validate();
        Dimension clientPref = getContentPane().getPreferredSize();
        this.pack();
        Insets in = getInsets();
        int totalW = clientPref.width  + in.left + in.right;
        int totalH = clientPref.height + in.top  + in.bottom + 40;
        this.setSize(totalW, totalH);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void showForEdit(Optional<IRule> rule) {
        this.rule = rule;
        try {
            this.populateForm(rule.get());
        } catch (IllegalArgumentException e) {
            mainView.showErrorMessage("Error in the form initialization");
        }
        this.saveButton.setAction(this.ruleFormDialogController.getSaveEditedRuleAction());
        this.setTitle("Edit selected rule");
        this.getContentPane().invalidate();
        this.getContentPane().validate();
        Dimension clientPref = getContentPane().getPreferredSize();
        this.pack();
        Insets in = getInsets();
        int totalW = clientPref.width  + in.left + in.right;
        int totalH = clientPref.height + in.top  + in.bottom + 40;
        this.setSize(totalW, totalH);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    private void populateForm(IRule rule) throws IllegalArgumentException {
        this.descriptionTextField.setText(rule.getDescription());
        this.directionComboBox.setSelectedItem(rule.getDirection().toString());
        
        String sourceIPRangeString = rule.getSourceIPRange().toString();
        JTextField[] octetFieldsSourceIPRange = { this.octet1SourceIPRange, this.octet2SourceIPRange, this.octet3SourceIPRange, this.octet4SourceIPRange };
        JTextField netmaskFieldSourceIPRange = this.netmaskSourceIPRange;
        String destinationIPRangeString = rule.getDestinationIPRange().toString();
        JTextField[] octetFieldsDestinationIPRange = { this.octet1DestinationIPRange, this.octet2DestinationIPRange, this.octet3DestinationIPRange, this.octet4DestinationIPRange };
        JTextField netmaskFieldDestinationIPRange = this.netmaskDestinationIPRange;
        try {
            this.populateIPFields(sourceIPRangeString, octetFieldsSourceIPRange, netmaskFieldSourceIPRange);
            this.populateIPFields(destinationIPRangeString, octetFieldsDestinationIPRange, netmaskFieldDestinationIPRange);
            this.populatePortFields(rule.getSourcePortRange().toString(), this.port1SourcePortRange, this.port2SourcePortRange);
            this.populatePortFields(rule.getDestinationPortRange().toString(), this.port1DestinationPortRange, this.port2DestinationPortRange);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The form could not be initialized due to an error in the input data for the field population");
        }
        
        this.protocolComboBox.setSelectedItem(rule.getProtocol().toString());
    }
    
    private void populateIPFields(String cidr, JTextField[] octetFields, JTextField netmaskField) throws IllegalArgumentException {
        try {
            // Split IP address and netmask
            String[] parts = cidr.split("/");
            String ipPart = parts[0];      // es. "192.168.1.0"
            String netmaskPart = parts[1]; // es. "24"

            // Get the octets
            String[] octets = ipPart.split("\\.");

            // Set each octet in its respective JTextField
            for (int i = 0; i < 4; i++) {
                octetFields[i].setText(octets[i]);
            }

            // Set the netmask
            netmaskField.setText(netmaskPart);

        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("IP address parsing error: %s", cidr));
        }
    }
    
    private void populatePortFields(String portRangeString, JTextField portStartField, JTextField portEndField) throws IllegalArgumentException {
        try {
            if (portRangeString.contains("-")) {
                String[] parts = portRangeString.split("-");
                portStartField.setText(parts[0].trim());
                portEndField.setText(parts[1].trim());
            } else {
                portStartField.setText(portRangeString.trim());
                portEndField.setText(portRangeString.trim());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Port parsing error: %s", portRangeString));
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        directionComboBox = new javax.swing.JComboBox<>();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        octet1SourceIPRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        octet2SourceIPRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        octet3SourceIPRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        octet4SourceIPRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        netmaskSourceIPRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        octet2DestinationIPRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        octet3DestinationIPRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        octet4DestinationIPRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        netmaskDestinationIPRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel12 = new javax.swing.JLabel();
        octet1DestinationIPRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel13 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel14 = new javax.swing.JLabel();
        port1DestinationPortRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel15 = new javax.swing.JLabel();
        port2DestinationPortRange = new javax.swing.JTextField();
        port1SourcePortRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel16 = new javax.swing.JLabel();
        port2SourcePortRange = new javax.swing.JTextField();
        javax.swing.JLabel jLabel17 = new javax.swing.JLabel();
        protocolComboBox = new javax.swing.JComboBox<>();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(617, 353));
        setSize(new java.awt.Dimension(617, 353));

        jLabel1.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel1.setText("Description:");

        descriptionTextField.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        descriptionTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);

        jLabel2.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel2.setText("Direction:");

        directionComboBox.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        directionComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "INBOUND", "OUTBOUND" }));

        jLabel3.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel3.setText("Source IP Range:");

        octet1SourceIPRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet1SourceIPRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet1SourceIPRange.setToolTipText("0");

        jLabel4.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(".");

        octet2SourceIPRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet2SourceIPRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet2SourceIPRange.setToolTipText("0");

        jLabel5.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(".");

        octet3SourceIPRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet3SourceIPRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet3SourceIPRange.setToolTipText("0");

        jLabel6.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText(".");

        octet4SourceIPRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet4SourceIPRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet4SourceIPRange.setToolTipText("0");

        jLabel7.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("/");

        netmaskSourceIPRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        netmaskSourceIPRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        netmaskSourceIPRange.setToolTipText("0");
        netmaskSourceIPRange.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        jLabel8.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText(".");

        octet2DestinationIPRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet2DestinationIPRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet2DestinationIPRange.setToolTipText("0");

        jLabel9.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText(".");

        octet3DestinationIPRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet3DestinationIPRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet3DestinationIPRange.setToolTipText("0");

        jLabel10.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText(".");

        octet4DestinationIPRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet4DestinationIPRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet4DestinationIPRange.setToolTipText("0");

        jLabel11.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("/");

        netmaskDestinationIPRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        netmaskDestinationIPRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        netmaskDestinationIPRange.setToolTipText("0");
        netmaskDestinationIPRange.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        jLabel12.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel12.setText("Destination IP Range:");

        octet1DestinationIPRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        octet1DestinationIPRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        octet1DestinationIPRange.setToolTipText("0");

        jLabel13.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel13.setText("Source Port Range:");

        jLabel14.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel14.setText("Destination Port Range:");

        port1DestinationPortRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        port1DestinationPortRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        port1DestinationPortRange.setToolTipText("0");

        jLabel15.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("-");

        port2DestinationPortRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        port2DestinationPortRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        port2DestinationPortRange.setToolTipText("0");

        port1SourcePortRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        port1SourcePortRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        port1SourcePortRange.setToolTipText("0");

        jLabel16.setFont(new java.awt.Font("JetBrains Mono", 0, 16)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("-");

        port2SourcePortRange.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        port2SourcePortRange.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        port2SourcePortRange.setToolTipText("0");

        jLabel17.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        jLabel17.setText("Protocol:");

        protocolComboBox.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        protocolComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TCP", "UDP", "ICMP" }));

        saveButton.setBackground(new java.awt.Color(28, 39, 76));
        saveButton.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        saveButton.setForeground(new java.awt.Color(255, 255, 255));
        saveButton.setText("Save");

        cancelButton.setBackground(new java.awt.Color(153, 153, 153));
        cancelButton.setFont(new java.awt.Font("JetBrains Mono", 0, 14)); // NOI18N
        cancelButton.setForeground(new java.awt.Color(255, 255, 255));
        cancelButton.setText("Cancel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel13)
                            .addComponent(jLabel17)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel12))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(descriptionTextField)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(protocolComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(port1SourcePortRange, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(port2SourcePortRange, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(port1DestinationPortRange, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(port2DestinationPortRange, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(octet1DestinationIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(octet2DestinationIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(octet3DestinationIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(octet4DestinationIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(netmaskDestinationIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(octet1SourceIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(octet2SourceIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(octet3SourceIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(octet4SourceIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(netmaskSourceIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(directionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(directionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(octet1SourceIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(octet2SourceIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(octet3SourceIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(octet4SourceIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(netmaskSourceIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(octet1DestinationIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(octet2DestinationIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(octet3DestinationIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(octet4DestinationIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(netmaskDestinationIPRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(port1SourcePortRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(port2SourcePortRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(port1DestinationPortRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(port2DestinationPortRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(protocolComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JComboBox<String> directionComboBox;
    private javax.swing.JTextField netmaskDestinationIPRange;
    private javax.swing.JTextField netmaskSourceIPRange;
    private javax.swing.JTextField octet1DestinationIPRange;
    private javax.swing.JTextField octet1SourceIPRange;
    private javax.swing.JTextField octet2DestinationIPRange;
    private javax.swing.JTextField octet2SourceIPRange;
    private javax.swing.JTextField octet3DestinationIPRange;
    private javax.swing.JTextField octet3SourceIPRange;
    private javax.swing.JTextField octet4DestinationIPRange;
    private javax.swing.JTextField octet4SourceIPRange;
    private javax.swing.JTextField port1DestinationPortRange;
    private javax.swing.JTextField port1SourcePortRange;
    private javax.swing.JTextField port2DestinationPortRange;
    private javax.swing.JTextField port2SourcePortRange;
    private javax.swing.JComboBox<String> protocolComboBox;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
