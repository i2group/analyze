/*
 * Copyright (c) 2014, 2021 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example.security.jaxb;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

/**
 * The default dimension value.
 */
@XmlType(name = "Permission")
public final class Permission
{
    @XmlAttribute(name = "DimensionValue", required = true)
    private String mDimensionValue;

    /**
     * Default constructor.
     */
    public Permission()
    {
    }

    /**
     * Permission constructor.
     * @param dimensionValue The dimension value.
     */
    public Permission(final String dimensionValue)
    {
        mDimensionValue = dimensionValue;
    }

    /**
     * @return The dimension value.
     */
    @XmlTransient
    public String getDimensionValue()
    {
        return mDimensionValue;
    }
}
