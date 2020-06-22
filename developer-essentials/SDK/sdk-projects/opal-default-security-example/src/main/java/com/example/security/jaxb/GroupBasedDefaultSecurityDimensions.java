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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * The {@link Collection} of {@link GroupPermissions}s. 
 */
@XmlRootElement(name = "GroupBasedDefaultSecurityDimensions")
@XmlType(name = "GroupBasedDefaultSecurityDimensions")
public class GroupBasedDefaultSecurityDimensions
{
    @XmlElement(name = "GroupPermissions", required = false)
    private Collection<GroupPermissions> mGroupPermissions;

    public GroupBasedDefaultSecurityDimensions()
    {
    }

    public GroupBasedDefaultSecurityDimensions(Collection<GroupPermissions> groupPermissions)
    {
        mGroupPermissions = groupPermissions;
    }

    @XmlTransient
    public Collection<GroupPermissions> getGroupPermissions()
    {
        return mGroupPermissions;
    }
}
