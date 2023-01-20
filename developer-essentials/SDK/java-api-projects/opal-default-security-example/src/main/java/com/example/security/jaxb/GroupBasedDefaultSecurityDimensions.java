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
