package it.unibas.softwarefirewall.firewallgui.view;

import com.google.inject.Singleton;
import it.unibas.softwarefirewall.firewallapi.IPacketLogEntry;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Singleton
public class PacketsDetailsTableModel extends AbstractTableModel {
    
    private final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
    private List<IPacketLogEntry> packetLogEntries = new ArrayList<>();
    
    private final String[] columnNames = {
        "Arrival time", "Allowed", "Packet"
    };

    @Override
    public int getRowCount() {
        return packetLogEntries.size();
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
      return switch (column) {
            case 0, 2 -> String.class;
            case 1 -> Boolean.class; 
            default -> Object.class;
        };
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        IPacketLogEntry ple = packetLogEntries.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> formatTimestamp(ple.getTimestamp());
            case 1 -> ple.getAllowed();
            case 2 -> ple.getPacket().toDisplayString();
            default -> null;
        };
    }
    
    public void updateContent() {
        this.fireTableDataChanged();
    }
    
    public String formatTimestamp(Long epochMillis) {
        LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis),
                                                   ZoneId.systemDefault());
        return dt.format(TIMESTAMP_FMT);
    }

}
