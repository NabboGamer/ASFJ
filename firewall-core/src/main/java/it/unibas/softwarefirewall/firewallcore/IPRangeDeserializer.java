package it.unibas.softwarefirewall.firewallcore;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class IPRangeDeserializer implements JsonDeserializer<IPRange> {
    @Override
    public IPRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return new IPRange(json.getAsString());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid CIDR: " + json.getAsString(), e);
        }
    }
}
