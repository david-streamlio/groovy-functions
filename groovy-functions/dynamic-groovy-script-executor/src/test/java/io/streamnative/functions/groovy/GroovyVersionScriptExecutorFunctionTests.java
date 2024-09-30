package io.streamnative.functions.groovy;

import org.apache.commons.io.IOUtils;
import org.apache.pulsar.functions.api.Context;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroovyVersionScriptExecutorFunctionTests {

    private GroovyVersionScriptExecutorFunction function;

    private Context mockContext;

    private Logger mockLogger;

    protected static String readFileAsString(String resourcePath) throws Exception {
        try (InputStream inputStream = GroovyVersionScriptExecutorFunctionTests.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }
            // Read the InputStream directly into a String using Apache Commons IO
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    @BeforeEach
    public final void init() throws Exception {
        mockContext = mock(Context.class);
        mockLogger = mock(Logger.class);

        // Set up the mock context to return the mock logger
        when(mockContext.getLogger()).thenReturn(mockLogger);

        function = new GroovyVersionScriptExecutorFunction();
    }

    @Test
    public final void arithmeticTest() throws Exception {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("groovyVersion", "3.0.22");
        jsonMessage.put("script", "3 + 5");

        String result = function.process(jsonMessage.toString(), mockContext);
        assertEquals("8", result);
    }

    @Test
    public final void arithmeticTestOldVersion() throws Exception {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("groovyVersion", "3.0.0");
        jsonMessage.put("script", "4 + 12");

        String result = function.process(jsonMessage.toString(), mockContext);
        assertEquals("16", result);
        verify(mockLogger).info("Downloading Groovy JAR from: https://repo1.maven.org/maven2/org/codehaus/groovy/groovy/3.0.0/groovy-3.0.0.jar");
    }

    @Test
    public final void functionTest() throws Exception {

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("groovyVersion", "3.0.15");
        jsonMessage.put("script", "def greet(name) { return 'Hello ' + name }; greet('Pulsar')");


        String result = function.process(jsonMessage.toString(), mockContext);
        assertEquals("Hello Pulsar", result);
    }

    @Test
    public final void parametersTest() throws Exception {
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("groovyVersion", "3.0.12");
        jsonMessage.put("script", "return \"${greeting}, ${name}!\"");
        jsonMessage.put("parameters", "{\n" +
                "    \"name\": \"Pulsar\",\n" +
                "    \"greeting\": \"Buenos Dias\"\n" +
                "  }");

        String result = function.process(jsonMessage.toString(), mockContext);
        assertEquals("Buenos Dias, Pulsar!", result);
    }

    @Test
    public final void grabTest() throws Exception {

        String groovy = "@Grab(group='org.apache.commons', module='commons-lang3', version='3.17.0')\n" +
                "import org.apache.commons.lang3.StringUtils\n" +
                "return StringUtils.reverse(\"Groovy Grape\")\n";

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("groovyVersion", "3.0.15");
        jsonMessage.put("script", groovy);

        String result = function.process(jsonMessage.toString(), mockContext);
        assertEquals("eparG yvoorG", result);
    }

    @Test
    public final void httpGetTest() throws Exception {
       String groovy = readFileAsString("scripts/HttpGetGroovyScript.groovy");

        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("groovyVersion", "3.0.15");
        jsonMessage.put("script", groovy);

        String result = function.process(jsonMessage.toString(), mockContext);
        assertNotNull(result);
        assertTrue(result.startsWith("Response Content:\n" +
                "{  \"userId\": 1,  \"id\": 1"));
    }
}
