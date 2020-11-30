/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.catalog;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.restgenerator.DtoMapping;
import com.byoskill.tools.springcrudgenerator.restgenerator.templates.DtoInformation;

import java.util.List;

public interface Catalog {

    void addEntitiy(ClassInformation entity);

    public List<DtoInformation> getDtos();

    public List<ClassInformation> getEntities();

    void addDto(DtoInformation dtoInformation);

    void addDtoMapping(DtoMapping dtoMapping);

    List<DtoMapping> getDtoMappings();
}
