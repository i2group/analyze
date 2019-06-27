/*
 * Copyright (c) 2014, 2019 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example.security.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * The default dimension value.
 */
@XmlType(name = "Permission")
public class Permission
{
    @XmlAttribute(name = "DimensionValue", required = true)
    private String mDimensionValue;

    public Permission()
    {
    }

    public Permission(String dimensionValue)
    {
        mDimensionValue = dimensionValue;
    }

    @XmlTransient
    public String getDimensionValue(){
        return mDimensionValue;
    }
}
