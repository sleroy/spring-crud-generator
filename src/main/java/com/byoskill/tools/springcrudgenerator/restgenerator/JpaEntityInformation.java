/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ClassInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.TypingInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class JpaEntityInformation {
    private ClassInformation        entityType;
    private Set<TypingInfo>         dependencies   = new HashSet<>();
    private List<ColumnInformation> simpleColumns  = new ArrayList<>();
    private List<ColumnInformation> complexColumns = new ArrayList<>();


    public JpaEntityInformation(final ClassInformation entityClassName) {

        entityType = entityClassName;
    }
}
