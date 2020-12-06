/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.catalog;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.restgenerator.JpaEntityInformation;

import java.util.List;

public interface Catalog {

    void addClassDefinition(ClassInformation entity);

    List<ClassInformation> getClassDefinitions();

    void addJpaEntity(JpaEntityInformation jpaEntityInformation);

    List<JpaEntityInformation> getJpaEntities();
}
