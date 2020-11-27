package com.byoskill.tools.springcrudgenerator.entityscanner;

import com.byoskill.tools.springcrudgenerator.model.FieldInformation;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FieldScanner {
    private final List<FieldInformation> fields;

    public FieldScanner(List<FieldInformation> fields) {

        this.fields = fields;
    }

    public void scan() {
        
    }
}
