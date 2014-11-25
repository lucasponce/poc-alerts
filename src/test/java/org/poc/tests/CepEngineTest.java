package org.poc.tests;

import org.junit.Test;
import org.poc.cep.CepEngine;
import org.poc.cep.CepEngineFactory;

/**
 * Basic tests for CepEngine
 */
public class CepEngineTest {

    @Test
    public void testAddRule() throws Exception {

        String dummyRule = "" +
                "package org.poc.rules \n\n" +
                "import org.poc.api.Alert\n" +
                "import org.poc.api.Event\n" +
                "import org.poc.api.State\n\n" +
                "global java.io.PrintStream out \n\n" +
                "rule \"Dummy\" \n" +
                "when \n" +
                "  e : Event( id == \"1\" ) \n" +
                "then \n" +
                "  out.println(\"Event(id == 1)\"); \n" +
                "end";

        CepEngine cepEngine = CepEngineFactory.getCepEngine();
        cepEngine.addRule("dummy", dummyRule);
    }
}
