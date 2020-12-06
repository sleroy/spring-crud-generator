/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.catalog;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.restgenerator.JpaEntityInformation;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class CatalogImpl implements Catalog {
    private final List<ClassInformation>     entities              = new ArrayList<>();
    private final List<JpaEntityInformation> jpaEntityInformations = new ArrayList<>();

    @Override
    public void addClassDefinition(final ClassInformation entity) {
        entities.add(entity);
    }

    @Override
    public List<ClassInformation> getClassDefinitions() {
        return unmodifiableList(entities);
    }

    @Override
    public void addJpaEntity(final JpaEntityInformation jpaEntityInformation) {
        jpaEntityInformations.add(jpaEntityInformation);
    }

    @Override
    public List<JpaEntityInformation> getJpaEntities() {
        return jpaEntityInformations;
    }

}
