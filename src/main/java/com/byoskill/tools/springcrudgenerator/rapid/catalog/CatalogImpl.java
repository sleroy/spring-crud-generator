/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.catalog;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.restgenerator.DtoMapping;
import com.byoskill.tools.springcrudgenerator.restgenerator.templates.DtoInformation;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class CatalogImpl implements Catalog {
    private final List<DtoInformation>   dtoInformations = new ArrayList<>();
    private final List<ClassInformation> entities        = new ArrayList<>();
    private final List<DtoMapping>       dtoMappings     = new ArrayList<>();

    @Override
    public void addEntitiy(final ClassInformation entity) {
        entities.add(entity);
    }

    @Override
    public List<DtoInformation> getDtos() {
        return unmodifiableList(dtoInformations);
    }

    @Override
    public List<ClassInformation> getEntities() {
        return unmodifiableList(entities);
    }

    @Override
    public void addDto(final DtoInformation dtoInformation) {
        dtoInformations.add(dtoInformation);
    }

    @Override
    public void addDtoMapping(final DtoMapping dtoMapping) {
        dtoMappings.add(dtoMapping);
    }

    @Override
    public List<DtoMapping> getDtoMappings() {
        return unmodifiableList(dtoMappings);
    }
}
