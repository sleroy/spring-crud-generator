/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator.templates;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.EntityInformation;
import lombok.Data;

import java.io.Serializable;

@Data
public class DtoInformation implements Serializable {
    private String            packageName;
    private String            className;
    private EntityInformation entityInformation;
}
