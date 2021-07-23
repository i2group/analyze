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
 * The collection of {@link Permissions}s by user group.
 */
@XmlType(name = "GroupPermissions")
public final class GroupPermissions
{
    private static final String ALL_USERS_GROUP = "*";

    @XmlAttribute(name = "UserGroup", required = true)
    private String mUserGroup;

    @XmlElement(name = "Permissions", required = false)
    private Collection<Permissions> mPermissions;

    /**
     * Default constructor.
     */
    public GroupPermissions()
    {
    }

    /**
     * GroupPermissions constructor.
     * @param userGroup The user group.
     * @param permissions The permissions.
     */
    public GroupPermissions(final String userGroup, final Collection<Permissions> permissions)
    {
        mUserGroup = userGroup;
        mPermissions = permissions;
    }

    /**
     * @return The user group.
     */
    @XmlTransient
    public String getUserGroup()
    {
        return mUserGroup;
    }

    /**
     * @return true if user group is all users.
     */
    @XmlTransient
    public boolean isAllUsers()
    {
        return ALL_USERS_GROUP.equals(mUserGroup);
    }

    /**
     * @return All group permissions.
     */
    @XmlTransient
    public Collection<Permissions> getAllGroupPermissions()
    {
        return mPermissions;
    }
}
