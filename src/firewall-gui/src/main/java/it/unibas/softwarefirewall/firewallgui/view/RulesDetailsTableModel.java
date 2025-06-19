package it.unibas.softwarefirewall.firewallgui.view;

import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallapi.IRule;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Singleton
public class RulesDetailsTableModel extends AbstractTableModel {
    
    private List<IRule> rules = new ArrayList<>();
    
    private final String[] columnNames = {
        "Description", "Direction",
        "Source IP Range", "Destination IP Range",
        "Source Port Range", "Destination Port Range",
        "Protocol"
    };

    @Override
    public int getRowCount() {
        return rules.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        IRule r = rules.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> r.getDescription();
            case 1 -> r.getDirection().toString();
            case 2 -> r.getSourceIPRange().toString();
            case 3 -> r.getDestinationIPRange().toString();
            case 4 -> r.getSourcePortRange().toString();
            case 5 -> r.getDestinationPortRange().toString();
            case 6 -> r.getProtocol().toString();
            default -> null;
        };
    }

    public void addRule(IRule rule) {
        rules.add(rule);
        int idx = rules.size() - 1;
        fireTableRowsInserted(idx, idx);
    }

    public void removeRule(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < rules.size()) {
            rules.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }
    
    public void updateContent() {
        this.fireTableDataChanged();
    }

}

