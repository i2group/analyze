/*
 * Copyright (c) 2014, 2017 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example.rest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * A request to the external data subset resource that this example contains.
 */
@XmlType(name = "SubsetRequest")
@XmlRootElement(name = "SubsetRequest")
public final class SubsetRequest
{
    @XmlAttribute(name = "SearchString")
    private String mSearchString;

    /**
     * Constructs a new {@link SubsetRequest}. This constructor is intended for
     * use by JAXB and should not be called by other code.
     * 
     * @exclude
     */
    public SubsetRequest()
    {
    }

    /**
     * Constructs a new {@link SubsetRequest} that contains the specified search
     * string.
     * 
     * @param searchString
     *            A {@link String} that defines the search that this
     *            {@link SubsetRequest} represents.
     * 
     */
    public SubsetRequest(final String searchString)
    {
        mSearchString = searchString;
    }

    /**
     * Gets the {@link String} that defines the search that this
     * {@link SubsetRequest} represents.
     * 
     * @return See above.
     */
    @XmlTransient
    public String getSearchString()
    {
        return mSearchString;
    }

    /**
     * Sets the {@link String} that defines the search that this
     * {@link SubsetRequest} represents.
     * 
     * @param searchString
     *            The {@link String} to be set.
     */
    public void setSearchString(final String searchString)
    {
        mSearchString = searchString;
    }
}
