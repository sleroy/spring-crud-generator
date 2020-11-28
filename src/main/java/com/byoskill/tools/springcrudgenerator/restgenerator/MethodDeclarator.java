/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import com.squareup.javapoet.MethodSpec;

import java.util.function.Supplier;

@FunctionalInterface
public interface MethodDeclarator extends Supplier<MethodSpec> {
}
