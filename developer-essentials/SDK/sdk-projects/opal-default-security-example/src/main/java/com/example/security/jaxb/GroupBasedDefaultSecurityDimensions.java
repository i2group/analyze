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

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

/**
 * The {@link Collection} of {@link GroupPermissions}s.
 */
@XmlRootElement(name = "GroupBasedDefaultSecurityDimensions")
@XmlType(name = "GroupBasedDefaultSecurityDimensions")
public final class GroupBasedDefaultSecurityDimensions
{
    @XmlElement(name = "GroupPermissions", required = false)
    private Collection<GroupPermissions> mGroupPermissions;

    /**
     * Default constructor.
     */
    public GroupBasedDefaultSecurityDimensions()
    {
    }

    /**
     * @param groupPermissions The group permissions.
     */
    public GroupBasedDefaultSecurityDimensions(final Collection<GroupPermissions> groupPermissions)
    {
        mGroupPermissions = groupPermissions;
    }

    /**
     * @return The group permissions.
     */
    @XmlTransient
    public Collection<GroupPermissions> getGroupPermissions()
    {
        return mGroupPermissions;
    }
}
