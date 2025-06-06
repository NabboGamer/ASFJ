package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.Range;
import lombok.Data;

@Data
public class PortRange implements Range<Integer> {
    private Integer start;
    private Integer end;

    public PortRange(int start, int end) throws IllegalArgumentException {
        if (start < 0 || end > 65535 || start > end) {
            throw new IllegalArgumentException("Port range must be between 0 and 65535, and start <= end");
        }
        this.start = start;
        this.end = end;
    }

    @Override
    public Boolean contains(Integer value) {
        return value >= this.start && value <= this.end;
    }

    @Override
    public String toString() {
        return this.start.equals(this.end) ? this.start.toString() : (this.start + "-" + this.end);
    }
}

