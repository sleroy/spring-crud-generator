/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator;


import com.byoskill.tools.example.Payment;
import com.byoskill.tools.springcrudgenerator.rapid.catalog.Catalog;
import com.byoskill.tools.springcrudgenerator.rapid.catalog.CatalogImpl;
import com.byoskill.tools.springcrudgenerator.restgenerator.JpaEntityScanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;

public class JpaEntityScannerTest {

    @Test
    public void generate() throws Exception {
        final Catalog catalog = new CatalogImpl();

        final JpaEntityScanner jpaEntityScanner = new JpaEntityScanner(catalog);
        jpaEntityScanner.generate(Payment.class);

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("catalog.json"), catalog);
    }
}