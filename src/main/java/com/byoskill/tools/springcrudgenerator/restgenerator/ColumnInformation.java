/*
 * Copyright (c) 2020. Byoskill Leroy (Sylvain Leroy).
 * All rights reserved.
 */

package com.byoskill.tools.springcrudgenerator.restgenerator;

import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.FieldInformation;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.ParameterizedTypeInfo;
import com.byoskill.tools.springcrudgenerator.rapid.reflectionscanner.model.TypingInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ColumnInformation extends FieldInformation {

    private boolean    isEntityCollection;
    private boolean    isEntityRelationship;
    private boolean    isManyToMany;
    private boolean    isEmbeddedId;
    private boolean    isOneToMany;
    private boolean    isManyToOne;
    private boolean    isOneToOne;
    private TypingInfo relatedJpaEntity;

    public static ColumnInformation fromField(final FieldInformation fieldInformation) {
        final ColumnInformation columnInformation = new ColumnInformation();
        columnInformation.setAnnotations(fieldInformation.getAnnotations());
        columnInformation.setExtraProperties(fieldInformation.getExtraProperties());
        columnInformation.setModifiers(fieldInformation.getModifiers());
        columnInformation.setName(fieldInformation.getName());
        columnInformation.setType(fieldInformation.getType());

        final boolean isManyToMany = columnInformation.isAnnotatedPropertyWith(ManyToMany.class);
        final boolean isOneToOne   = columnInformation.isAnnotatedPropertyWith(OneToOne.class);
        final boolean isManyToOne  = columnInformation.isAnnotatedPropertyWith(ManyToOne.class);
        final boolean isOneToMany  = columnInformation.isAnnotatedPropertyWith(OneToMany.class);
        final boolean isEmbeddedId = columnInformation.isAnnotatedPropertyWith(EmbeddedId.class);


        columnInformation.isManyToMany = isManyToMany;
        columnInformation.isOneToOne = isOneToOne;
        columnInformation.isManyToOne = isManyToOne;
        columnInformation.isOneToMany = isOneToMany;
        columnInformation.isEmbeddedId = isEmbeddedId;


        final boolean isEntityRelationship = isManyToMany
                || isOneToOne
                || isManyToOne
                || isOneToMany
                || isEmbeddedId;

        columnInformation.setEntityRelationship(isEntityRelationship);
        try {
            columnInformation.setEntityCollection(CodeGeneratorUtils.isCollection(fieldInformation.getType()));
        } catch (final ClassNotFoundException e) {
            log.error("ClassNotFoundException ", e);
        }

        if (isEntityRelationship) {
            if (columnInformation.isEntityCollection) {
                columnInformation.relatedJpaEntity = ((ParameterizedTypeInfo) columnInformation.getType()).getTypeParameters().get(0);
            } else {
                columnInformation.relatedJpaEntity = columnInformation.getType();
            }
        }

        return columnInformation;
    }

}
