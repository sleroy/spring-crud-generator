/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator.templates;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.FieldInformation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DtoInformation implements Serializable {

    private ClassInformation originalEntity;
    private ClassInformation dto = new ClassInformation();

    public void setFields(final List<FieldInformation> lightDtoFields) {
        dto.setFields(lightDtoFields);
    }
}
