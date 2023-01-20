/*
 * MIT License
 *
 * Copyright (c) 2023, N. Harris Computer Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
