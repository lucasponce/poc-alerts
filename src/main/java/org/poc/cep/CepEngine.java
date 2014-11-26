package org.poc.cep;

import java.util.Collection;

/**
 * Interface that defines main API between AlertsService and CEP engine implementation.
 */
public interface CepEngine {

    void addRule(String id, String rule);

    void addGlobal(String name, Object global);

    void addFact(Object fact);

    void fire();

}
