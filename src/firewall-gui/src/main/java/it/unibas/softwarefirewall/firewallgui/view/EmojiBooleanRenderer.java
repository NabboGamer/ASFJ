package it.unibas.softwarefirewall.firewallgui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class EmojiBooleanRenderer extends DefaultTableCellRenderer {
    
    private final Icon checkmarkIcon;
    private final Icon crossIcon;

    public EmojiBooleanRenderer() {
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.checkmarkIcon = new FlatSVGIcon("images/check_mark.svg", 18, 18);
        this.crossIcon = new FlatSVGIcon("images/cross_mark.svg", 18, 18);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        // Call the superclass method to handle selection, focus, colors, etc.
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        setText(null);

        if (value instanceof Boolean allowed) {
            this.setIcon(allowed ? checkmarkIcon : crossIcon);
        } else {
            this.setIcon(null);
        }

        return this;
    }
}
