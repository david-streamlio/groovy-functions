package io.streamnative.functions.groovy;

import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import groovy.lang.GroovyShell;

public class GroovyScriptExecutorFunction implements Function<String, String> {

    private GroovyShell groovyShell;

    @Override
    public String process(String script, Context ctx) throws Exception {
        try {
            // Evaluate the Groovy script passed as the message
            Object result = groovyShell.evaluate(script);

            // Log the result and return it as output
            ctx.getLogger().info("Script executed successfully. Result: " + result);
            return result != null ? result.toString() : "No result from script";

        } catch (Exception e) {
            // Log any error during script execution
            ctx.getLogger().error("Error executing Groovy script: ", e);
            throw e;
        }
    }

    @Override
    public void initialize(Context context) throws Exception {
        Function.super.initialize(context);

        // Initialize the Groovy shell to execute the script
        groovyShell = new GroovyShell();
    }

    @Override
    public void close() throws Exception {
        Function.super.close();
    }
}
