package org.poc.tests;

import org.junit.Test;
import org.poc.api.Event;
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
                "  e : Event( ) \n" +
                "then \n" +
                "  out.println( e ); \n" +
                "end";

        CepEngine cepEngine = CepEngineFactory.getCepEngine();
        cepEngine.addRule("dummy", dummyRule);
        cepEngine.addGlobal("out", System.out);
        cepEngine.addFact( new Event("1", 0d, System.currentTimeMillis()) );
        cepEngine.fire();

        cepEngine.addFact( new Event("2", 0d, System.currentTimeMillis()) );
        cepEngine.fire();
    }
}
