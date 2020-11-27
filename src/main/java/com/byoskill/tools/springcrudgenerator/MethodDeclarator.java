package com.byoskill.tools.springcrudgenerator;

import com.squareup.javapoet.MethodSpec;

import java.util.function.Supplier;

@FunctionalInterface
public interface MethodDeclarator extends Supplier<MethodSpec> {
}
