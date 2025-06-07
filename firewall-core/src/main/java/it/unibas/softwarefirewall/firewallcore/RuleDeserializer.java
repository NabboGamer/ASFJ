package it.unibas.softwarefirewall.firewallcore;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unibas.softwarefirewall.firewallapi.Direction;
import it.unibas.softwarefirewall.firewallapi.Protocol;
import java.lang.reflect.Type;

public class RuleDeserializer implements JsonDeserializer<Rule> {
    @Override
    public Rule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        Rule rule = new Rule();
        rule.setID(obj.get("ID").getAsString());
        rule.setDescription(obj.get("description").getAsString());
        rule.setDirection(Direction.valueOf(obj.get("direction").getAsString()));

        // IP Ranges
        rule.setSourceIPRange(new IPRange(obj.get("sourceIPRange").getAsString()));
        rule.setDestinationIPRange(new IPRange(obj.get("destinationIPRange").getAsString()));

        // Port Ranges
        rule.setSourcePortRange(this.parsePortRange(obj.get("sourcePortRange").getAsString()));
        rule.setDestinationPortRange(this.parsePortRange(obj.get("destinationPortRange").getAsString()));

        // Protocol
        rule.setProtocol(Protocol.valueOf(obj.get("protocol").getAsString()));

        return rule;
    }

    private PortRange parsePortRange(String text) {
        if (text.contains("-")) {
            String[] parts = text.split("-");
            int start = Integer.parseInt(parts[0].trim());
            int end = Integer.parseInt(parts[1].trim());
            return new PortRange(start, end);
        } else {
            int p = Integer.parseInt(text.trim());
            return new PortRange(p, p);
        }
    }
}
