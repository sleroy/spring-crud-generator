/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.restgenerator.templates.DtoInformation;
import lombok.Data;

@Data
public class DtoMapping {
    private ClassInformation entityType;
    private DtoInformation   dtoType;
    private DtoInformation   dtoLightType;


    public DtoMapping(final ClassInformation entityClassName) {

        entityType = entityClassName;
    }
}
