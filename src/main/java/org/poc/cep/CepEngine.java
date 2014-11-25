package org.poc.cep;

import java.util.Collection;

/**
 * Interface that defines main API between AlertsService and CEP engine implementation.
 */
public interface CepEngine {

    void addRule(String rule);

    void startStatefulSession();

    void startStatelessSession();

    void addFact(Object fact);

    void addFact(Collection<Object> facts);

    void fire();

}
