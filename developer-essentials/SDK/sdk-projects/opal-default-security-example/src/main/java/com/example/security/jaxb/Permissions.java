/*
 * Copyright (c) 2014, 2020 IBM Corp.
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * The default security dimension and {@link Permission}s.
 */
@XmlType(name = "Permissions")
public class Permissions
{
    @XmlAttribute(name = "Dimension", required = true)
    private String mDimension;

    @XmlElement(name = "Permission", required = false)
    private Collection<Permission> mValues;

    public Permissions()
    {
    }

    public Permissions(String dimension, Collection<Permission> values)
    {
        mDimension = dimension;
        mValues = values;
    }

    @XmlTransient
    public String getDimension()
    {
        return mDimension;
    }

    @XmlTransient
    public Collection<Permission> getPermissions()
    {
        return mValues;
    }
}
