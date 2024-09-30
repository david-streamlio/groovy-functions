package io.streamnative.sources.data;

import org.json.JSONObject;
import java.util.*;

public class RandomScriptRepository implements GroovyScriptRepository {

    private static final List<String> SCRIPTS = new LinkedList<String>();

    static {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("groovyVersion", "3.0.22");
        jsonMessage.put("script", "3 + 5");
        SCRIPTS.add(jsonMessage.toString());

        jsonMessage = new JSONObject();
        jsonMessage.put("groovyVersion", "3.0.0");
        jsonMessage.put("script", "4 + 12");
        SCRIPTS.add(jsonMessage.toString());

        jsonMessage = new JSONObject();
        jsonMessage.put("groovyVersion", "3.0.15");
        jsonMessage.put("script", "def greet(name) { return 'Hello ' + name }; greet('Pulsar')");
        SCRIPTS.add(jsonMessage.toString());
    }

    private final Random random = new Random();

    @Override
    public String get() {
        return SCRIPTS.get(random.nextInt(SCRIPTS.size()));
    }
}
