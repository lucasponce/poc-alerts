package org.poc.impl;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.poc.cep.CepEngine;

import java.util.Collection;

/**
 * A proof of concept of CepEngine to test several options of interaction between high level API and drools API.
 */
public class PocCepEngine implements CepEngine {

    private KieServices ks;
    private KieRepository kr;
    private KieFileSystem kfs;
    private KieBuilder kb;
    private KieContainer kc;
    private KieSession kSession;

    private static final String PATH = "src/main/resources/org/poc/rules";

    private String getPath(String id) {
        return PATH + "/" + id + ".drl";
    }

    private void initKieArtifacts() {
        ks = KieServices.Factory.get();
        kr = ks.getRepository();
        kfs = ks.newKieFileSystem();
    }

    @Override
    public void addRule(String id, String rule) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        if (kfs == null) {
            initKieArtifacts();
        }
        String path = getPath(id);
        if (kfs.read(path) != null) {
            throw new IllegalArgumentException("Id argument exists on current repository");
        }

        kfs.write(path, rule);

        kb = ks.newKieBuilder(kfs);

        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        kc = ks.newKieContainer(kr.getDefaultReleaseId());
    }

    @Override
    public void startStatefulSession() {

    }

    @Override
    public void startStatelessSession() {

    }

    @Override
    public void addFact(Object fact) {

    }

    @Override
    public void addFact(Collection<Object> facts) {

    }

    @Override
    public void fire() {

    }
}
