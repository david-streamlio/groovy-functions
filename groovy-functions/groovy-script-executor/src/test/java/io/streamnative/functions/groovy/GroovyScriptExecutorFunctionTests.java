package io.streamnative.functions.groovy;

import org.apache.pulsar.functions.api.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GroovyScriptExecutorFunctionTests {

    private GroovyScriptExecutorFunction function;
    private Context mockContext;

    @BeforeEach
    public final void init() throws Exception {
        mockContext = mock(Context.class);
        // Mock logger in the context (optional, based on your use case)
        when(mockContext.getLogger()).thenReturn(Mockito.mock(org.slf4j.Logger.class));

        function = new GroovyScriptExecutorFunction();
        function.initialize(mockContext);
    }

    @Test
    public final void arithmeticTest() throws Exception {
        String groovy = "3 + 6";
        String result = function.process(groovy, mockContext);
        assertEquals("9", result);
    }

    @Test
    public final void functionTest() throws Exception {
        String groovy = "def greet(name) {\n" +
                "    return \"Hello, \" + name\n" +
                "}\n" +
                "\n" +
                "greet(\"Pulsar\")\n";

        String result = function.process(groovy, mockContext);
        assertEquals("Hello, Pulsar", result);
    }

    @Test
    public final void grabTest() throws Exception {
        String groovy = "@Grab(group='org.apache.commons', module='commons-lang3', version='3.17.0')\n" +
                "import org.apache.commons.lang3.StringUtils\n" +
                "\n" +
                "return StringUtils.reverse(\"Groovy Grape\")\n";

        String result = function.process(groovy, mockContext);
        assertEquals("eparG yvoorG", result);
    }
}
