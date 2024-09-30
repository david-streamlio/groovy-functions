package io.streamnative.functions.groovy;

import org.apache.ivy.util.StringUtils;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.util.StringUtil;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class GroovyVersionScriptExecutorFunction implements Function<String, String> {

    @Override
    public String process(String input, Context ctx) throws Exception {
        // Parse the input message (JSON) to get groovyVersion, script, and parameters
        Map<String, Object> groovy = parseJson(input);
        String groovyVersion = (String) groovy.get("groovyVersion");
        String script = (String) groovy.get("script");
        Binding binding = null;

        if (groovy.containsKey("parameters")) {
            binding = getBinding((String) groovy.get("parameters"));
        }

        // Load the correct version of Groovy dynamically based on the message
        GroovyShell groovyShell = loadGroovyShell(groovyVersion, binding, ctx);

        if (groovyShell == null) {
            ctx.getLogger().error("Failed to load Groovy version: " + groovyVersion);
            return "Error: Could not load Groovy version " + groovyVersion;
        }

        try {
            // Evaluate the script
            Object result = groovyShell.evaluate(script);
            ctx.getLogger().info("Script executed successfully with Groovy " + groovyVersion + ". Result: " + result);
            return result != null ? result.toString() : "No result from script";
        } catch (Exception e) {
            ctx.getLogger().error("Error executing Groovy script: ", e);
            throw e;
        }
    }

    private Map<String, Object> parseJson(String json) {
        org.json.JSONObject jsonObject = new org.json.JSONObject(json);
        return jsonObject.toMap();
    }

    private Binding getBinding(String parameters) {
        Binding binding = null;

        if (parameters != null && parameters.length() > 0) {
            Map<String, Object> params = parseJson(parameters);
            binding = new Binding();

            // Iterate over the map and set variables in the binding
            params.forEach(binding::setVariable);
        }

        return binding;

    }

    private GroovyShell loadGroovyShell(String version, Binding binding, Context ctx) {
        try {
            // URL to Maven Central or your own repository with Groovy JARs
            // https://repo1.maven.org/maven2/org/codehaus/groovy/groovy/3.0.15/groovy-3.0.15.jar
            String groovyJarUrl = "https://repo1.maven.org/maven2/org/codehaus/groovy/groovy/" + version + "/groovy-" + version + ".jar";
            ctx.getLogger().info("Downloading Groovy JAR from: " + groovyJarUrl);

            // Create temporary JAR file in memory
            URL jarUrl = new URL(groovyJarUrl);
            try (InputStream in = jarUrl.openStream()) {
                // Use a temporary location in memory or in some managed storage if needed
                java.nio.file.Path tempJar = Files.createTempFile("groovy-all-" + version, ".jar");
                Files.copy(in, tempJar, StandardCopyOption.REPLACE_EXISTING);
                ctx.getLogger().info("Downloaded Groovy JAR to: " + tempJar);

                // Load the JAR file using a URLClassLoader
                URL[] urls = {tempJar.toUri().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls, GroovyShell.class.getClassLoader());

                // Load the GroovyShell class from the dynamically loaded version
                Class<?> groovyShellClass = classLoader.loadClass("groovy.lang.GroovyShell");

                if (binding != null) {
                    return (GroovyShell) groovyShellClass.getDeclaredConstructor(Binding.class).newInstance(binding);
                } else {
                    // Create a new instance of GroovyShell using reflection
                    return (GroovyShell) groovyShellClass.getDeclaredConstructor().newInstance();
                }
            }
        } catch (Exception e) {
            ctx.getLogger().error("Error loading Groovy version " + version + ": ", e);
            return null;
        }
    }

}

