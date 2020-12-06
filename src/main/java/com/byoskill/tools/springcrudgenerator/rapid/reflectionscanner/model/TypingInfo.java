/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public abstract class TypingInfo implements Serializable {
    private String signature;
    private String variant;


    public boolean isParameterizedType() {
        return false;
    }
    public abstract Class<?> asClass() throws ClassNotFoundException;
}
