/*
 * Copyright (c) 2014, 2018 IBM Corp.
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.i2group.apollo.externaldata.transport.ExternalDataSubsetIdentifier;

/**
 * A response from the external data subset resource that this example contains.
 */
@XmlType(name = "SubsetResponse")
@XmlRootElement(name = "SubsetResponse")
public final class SubsetResponse
{
    @XmlElement(name = "SubsetIdentifier")
    private ExternalDataSubsetIdentifier mSubsetIdentifier;

    @XmlAttribute(name = "DataSourceId")
    private String mDataSourceId;

    /**
     * Constructs a new {@link SubsetResponse}. This constructor is intended for
     * use by JAXB and should not be called by other code.
     * 
     * @exclude
     */
    public SubsetResponse()
    {
    }

    /**
     * Constructs a new {@link SubsetResponse} that contains information about
     * a created subset.
     * 
     * @param subsetIdentifier
     *            The {@link ExternalDataSubsetIdentifier} of the created
     *            subset.
     * @param dataSourceId
     *            A {@link String} that contains the data source identifier.
     */
    public SubsetResponse(final ExternalDataSubsetIdentifier subsetIdentifier,
            final String dataSourceId)
    {
        mSubsetIdentifier = subsetIdentifier;
        mDataSourceId = dataSourceId;
    }

    /**
     * Gets the {@link ExternalDataSubsetIdentifier} of the created subset.
     * 
     * @return See above.
     */
    @XmlTransient
    public ExternalDataSubsetIdentifier getSubsetIdentifier()
    {
        return mSubsetIdentifier;
    }

    /**
     * Sets the {@link ExternalDataSubsetIdentifier} of the created subset.
     * 
     * @param subsetIdentifier
     *            The {@link ExternalDataSubsetIdentifier} to be set.
     */
    public void setSubsetIdentifier(
            final ExternalDataSubsetIdentifier subsetIdentifier)
    {
        mSubsetIdentifier = subsetIdentifier;
    }

    /**
     * Gets a {@link String} that contains the data source identifier.
     * 
     * @return See above.
     */
    @XmlTransient
    public String getDataSourceId()
    {
        return mDataSourceId;
    }

    /**
     * Sets the the data source identifier to the specified {@link String}.
     * 
     * @param dataSourceId
     *            A {@link String} that contains the data source identifier
     *            to be set.
     */
    public void setDataSourceId(final String dataSourceId)
    {
        mDataSourceId = dataSourceId;
    }
}
