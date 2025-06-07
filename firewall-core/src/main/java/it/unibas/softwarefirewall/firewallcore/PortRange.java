package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.Range;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class PortRange implements Range<Integer>, Cloneable {
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
    
    @Override
    public Object clone(){
        try {
            PortRange clonedPortRange = (PortRange)super.clone();
            clonedPortRange.setStart(this.start);
            clonedPortRange.setEnd(this.end);
            return clonedPortRange;
        } catch (CloneNotSupportedException cnse) {
            log.error("Error: not clonable object: {}", cnse);
            return null;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        PortRange otherPortRange = (PortRange) o;
        return this.start == otherPortRange.start && this.end == otherPortRange.end;
    }

    @Override
    public int hashCode() {
        int result = (this.start != null ? this.start.hashCode() : 0);
        result = 31 * result + (this.end != null ? this.end.hashCode() : 0);
        return result;
    }
}

