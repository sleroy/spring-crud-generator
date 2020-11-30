/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import lombok.Data;

@Data
public class EntityDtoGeneratorOptions {
    private String           modelPackageName;
    private String           outputFolder;
}
