package it.unibas.softwarefirewall.firewallcore;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class PortRangeDeserializer implements JsonDeserializer<PortRange> {
    @Override
    public PortRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String text = json.getAsString();
        try {
            if (text.contains("-")) {
                String[] parts = text.split("-");
                int start = Integer.parseInt(parts[0].trim());
                int end   = Integer.parseInt(parts[1].trim());
                return new PortRange(start, end);
            } else {
                int p = Integer.parseInt(text.trim());
                return new PortRange(p, p);
            }
        } catch (Exception e) {
            throw new JsonParseException("Invalid PortRange: " + text, e);
        }
    }
}