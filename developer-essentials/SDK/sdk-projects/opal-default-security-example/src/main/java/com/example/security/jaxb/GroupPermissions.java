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

import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * The collection of {@link Permissions}s by user group.
 */
@XmlType(name = "GroupPermissions")
public class GroupPermissions
{
    private static final String ALL_USERS_GROUP = "*";

    @XmlAttribute(name = "UserGroup", required = true)
    private String mUserGroup;

    @XmlElement(name = "Permissions", required = false)
    private Collection<Permissions> mPermissions;

    public GroupPermissions()
    {
    }

    public GroupPermissions(String userGroup, Collection<Permissions> permissions)
    {
        mUserGroup = userGroup;
        mPermissions = permissions;
    }

    @XmlTransient
    public String getUserGroup()
    {
        return mUserGroup;
    }

    @XmlTransient
    public boolean isAllUsers()
    {
        return ALL_USERS_GROUP.equals(mUserGroup);
    }

    @XmlTransient
    public Collection<Permissions> getAllGroupPermissions()
    {
        return mPermissions;
    }
}
