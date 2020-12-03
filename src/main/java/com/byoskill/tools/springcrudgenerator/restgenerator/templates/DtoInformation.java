/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator.templates;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.FieldInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.TypingInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class DtoInformation implements Serializable {

    private ClassInformation originalEntity;
    private ClassInformation dto = new ClassInformation();
    private Set<TypingInfo>  dependencies;

    public void setFields(final List<FieldInformation> lightDtoFields) {
        dto.setFields(lightDtoFields);
    }

}
