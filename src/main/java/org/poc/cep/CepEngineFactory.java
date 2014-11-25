package org.poc.cep;

import org.poc.impl.PocCepEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic factory for CepEngine implementations
 */
public class CepEngineFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CepEngineFactory.class);

    private static CepEngine cepEngine;

    public static CepEngine getCepEngine() {
        if (cepEngine == null) {
            cepEngine = new PocCepEngine();
        }
        return cepEngine;
    }


}
