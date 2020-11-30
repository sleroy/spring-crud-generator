/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model;


public class UnparseableTypingInfo extends TypingInfo {
    public static final TypingInfo INSTANCE         = new UnparseableTypingInfo();
    public static final String     CANNOT_BE_PARSED = "!!cannot be parsed!!";
    public static final String     UNPARSEABLE      = "unparseable";

    public UnparseableTypingInfo() {
        super(CANNOT_BE_PARSED, UNPARSEABLE);
    }
}
