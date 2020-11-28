/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class AnnotationInformation implements Serializable {
    private String              qualifiedName;
    private String              simpleName;
    private String              packageName;
    private Map<String, String> memberValue = new HashMap<>();

    public AnnotationInformation(final String qualifiedName, final String simpleName) {

        this.qualifiedName = qualifiedName;
        this.simpleName = simpleName;
    }

    public void addMemberValue(final String memberName, final String memberValueAsJSON) {
        memberValue.put(memberName, memberValueAsJSON);
    }


}
