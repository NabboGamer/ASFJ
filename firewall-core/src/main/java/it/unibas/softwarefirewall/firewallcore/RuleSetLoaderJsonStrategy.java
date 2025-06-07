package it.unibas.softwarefirewall.firewallcore;

import it.unibas.softwarefirewall.firewallapi.IRuleSetLoaderStrategy;
import it.unibas.softwarefirewall.firewallapi.IRule;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Singleton;

@Singleton
public class RuleSetLoaderJsonStrategy implements IRuleSetLoaderStrategy {
    private final Gson gson;

    public RuleSetLoaderJsonStrategy() {
        this.gson = new GsonBuilder().registerTypeAdapter(IPRange.class, new IPRangeDeserializer())
                                     .registerTypeAdapter(PortRange.class, new PortRangeDeserializer())
                                     .create();
    }

    @Override
    public List<IRule> load(String source) throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(source).toAbsolutePath())) {
            Type listType = new TypeToken<List<Rule>>(){}.getType();
            return gson.fromJson(reader, listType);
        }
    }
}
