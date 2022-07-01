/*
 * MIT License
 *
 * Copyright (c) 2022, N. Harris Computer Corporation
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
