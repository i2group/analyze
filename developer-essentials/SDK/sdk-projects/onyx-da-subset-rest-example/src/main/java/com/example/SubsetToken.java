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
package com.example;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * A JAXB class that represents the subset token.
 */
@XmlType(name = "SubsetToken")
@XmlRootElement(name = "SubsetToken")
public final class SubsetToken
{
    @XmlElement(name = "UniquePrefix", required = true)
    private String mUniquePrefix;

    @XmlElement(name = "PathToSubset", required = true)
    private String mPathToSubset;

    /**
     * Constructs a new {@link SubsetToken}. This constructor is intended for
     * use by JAXB and should not be called by other code.
     * 
     * @exclude
     */
    public SubsetToken()
    {
    }

    /**
     * Constructs a new {@link SubsetToken}.
     * 
     * @param uniquePrefix
     *            A {@link String} that contains a unique prefix for the subset
     *            that this {@link SubsetToken} represents.
     * @param pathToSubset
     *            A {@link String} that contains the path to the subset that
     *            this {@link SubsetToken} represents.
     */
    public SubsetToken(final String uniquePrefix, final String pathToSubset)
    {
        mUniquePrefix = uniquePrefix;
        mPathToSubset = pathToSubset;
    }

    /**
     * Sets the unique prefix for the subset that this {@link SubsetToken}
     * represents to the specified {@link String}.
     * 
     * @param uniquePrefix
     *            A {@link String} that contains a unique prefix for the subset
     *            that this {@link SubsetToken} represents.
     */
    public void setUniquePrefix(final String uniquePrefix)
    {
        mUniquePrefix = uniquePrefix;
    }

    /**
     * Gets a {@link String} that contains the unique prefix for the subset
     * that this {@link SubsetToken} represents.
     * 
     * @return See above.
     */
    @XmlTransient
    public String getUniquePrefix()
    {
        return mUniquePrefix;
    }

    /**
     * Sets the path to the subset that this {@link SubsetToken} represents to
     * the specified {@link String}.
     * 
     * @param pathToSubset
     *            A {@link String} that contains the path to the subset that
     *            this {@link SubsetToken} represents.
     */
    public void setPathToSubset(final String pathToSubset)
    {
        mPathToSubset = pathToSubset;
    }

    /**
     * Gets a {@link String} that contains the path to the subset that this
     * {@link SubsetToken} represents.
     * 
     * @return See above.
     */
    @XmlTransient
    public String getPathToSubset()
    {
        return mPathToSubset;
    }
}
