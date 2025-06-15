package it.unibas.softwarefirewall.firewallgui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.google.inject.Singleton;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class MainPanel extends JPanel {

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
        
        // Configure Second Tab
        this.mainPanelTabbedPane.addTab(null, this.secondTabPanel);
        JLabel tabLabel2 = new JLabel("Firewall Test");
        FlatSVGIcon hammerSVG = new FlatSVGIcon("images/hammer.svg", 18, 18);
        tabLabel2.setIcon(hammerSVG);
        tabLabel2.setIconTextGap(5);
        tabLabel2.setHorizontalTextPosition(SwingConstants.LEFT);
        this.mainPanelTabbedPane.setTabComponentAt(1, tabLabel2);
    }
    
    private ImageIcon createScaledIcon(String path, int width, int height) {
    URL imageUrl = getClass().getResource(path);
    if (imageUrl == null) {
        log.error("Immagine non trovata nel path: {}", path);
        return null;
    }

    ImageIcon originalIcon = new ImageIcon(imageUrl);
    Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    return new ImageIcon(scaledImage);
}


    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        firstTabPanel = new javax.swing.JPanel();
        secondTabPanel = new javax.swing.JPanel();
        mainPanelTabbedPane = new javax.swing.JTabbedPane();

        firstTabPanel.setName("firstTabPanel"); // NOI18N
        firstTabPanel.setPreferredSize(new java.awt.Dimension(1280, 720));

        javax.swing.GroupLayout firstTabPanelLayout = new javax.swing.GroupLayout(firstTabPanel);
        firstTabPanel.setLayout(firstTabPanelLayout);
        firstTabPanelLayout.setHorizontalGroup(
            firstTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1280, Short.MAX_VALUE)
        );
        firstTabPanelLayout.setVerticalGroup(
            firstTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 720, Short.MAX_VALUE)
        );

        secondTabPanel.setName("secondTabPanel"); // NOI18N
        secondTabPanel.setPreferredSize(new java.awt.Dimension(1280, 720));

        javax.swing.GroupLayout secondTabPanelLayout = new javax.swing.GroupLayout(secondTabPanel);
        secondTabPanel.setLayout(secondTabPanelLayout);
        secondTabPanelLayout.setHorizontalGroup(
            secondTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1280, Short.MAX_VALUE)
        );
        secondTabPanelLayout.setVerticalGroup(
            secondTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 720, Short.MAX_VALUE)
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
    private javax.swing.JPanel firstTabPanel;
    private javax.swing.JTabbedPane mainPanelTabbedPane;
    private javax.swing.JPanel secondTabPanel;
    // End of variables declaration//GEN-END:variables
}
