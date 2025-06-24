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
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class RuleSetLoaderJsonStrategy implements IRuleSetLoaderStrategy {
    private final Gson gson;

    public RuleSetLoaderJsonStrategy() {
        this.gson = new GsonBuilder().registerTypeAdapter(Rule.class, new RuleDeserializer())
                                     .create();
    }
    
    @Override
    public List<IRule> load(String source) throws IOException, FileNotFoundException, JsonSyntaxException {
        Reader reader;
        if (source.startsWith("classpath:")) {
            String res = source.substring("classpath:".length());
            InputStream in = getClass().getClassLoader().getResourceAsStream(res);
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + res);
            }
            reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        } else {
            Path p = Paths.get(source).toAbsolutePath();
            reader = Files.newBufferedReader(p);
        }
        try (reader) {
            Type listType = new TypeToken<List<Rule>>(){}.getType();
            return gson.fromJson(reader, listType);
        }
    }
    
}
