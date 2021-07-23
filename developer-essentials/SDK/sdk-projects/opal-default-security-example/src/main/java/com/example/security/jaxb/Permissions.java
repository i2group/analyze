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

import java.util.Collection;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

/**
 * The default security dimension and {@link Permission}s.
 */
@XmlType(name = "Permissions")
public final class Permissions
{
    @XmlAttribute(name = "Dimension", required = true)
    private String mDimension;

    @XmlElement(name = "Permission", required = false)
    private Collection<Permission> mValues;

    /**
     * Default constructor.
     */
    public Permissions()
    {
    }

    /**
     * Permission constructor.
     * @param dimension The dimension.
     * @param values The values.
     */
    public Permissions(final String dimension, final Collection<Permission> values)
    {
        mDimension = dimension;
        mValues = values;
    }

    /**
     * @return The dimensions.
     */
    @XmlTransient
    public String getDimension()
    {
        return mDimension;
    }

    /**
     * @return The permissions.
     */
    @XmlTransient
    public Collection<Permission> getPermissions()
    {
        return mValues;
    }
}
