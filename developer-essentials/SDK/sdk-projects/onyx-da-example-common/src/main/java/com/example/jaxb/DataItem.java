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
package com.example.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * A generic JAXB representation of Actor and Association elements in the sample
 * XML data that contains the features common to both.
 */
@XmlType(name = "DataItem")
@XmlSeeAlso({ Actor.class, Association.class })
public abstract class DataItem
{
    @XmlAttribute(name = "id", required = true)
    private String mId;

    @XmlElement(name = "RecordCreated", required = true)
    private String mRecordCreated;

    @XmlElement(name = "RecordModified", required = true)
    private String mRecordModified;

    /**
     * Constructs a new {@link DataItem}. This constructor is intended for use
     * by JAXB and should not be called by other code.
     * 
     * @exclude
     */
    protected DataItem()
    {
    }

    /**
     * Sets the identifier of this {@link DataItem} to the specified
     * {@link String}.
     * 
     * @param id
     *            A {@link String} that contains the identifier to set.
     */
    public final void setId(final String id)
    {
        mId = id;
    }

    /**
     * Gets a {@link String} that contains the identifier of this
     * {@link DataItem}.
     * 
     * @return See above.
     */
    @XmlTransient
    public final String getId()
    {
        return mId;
    }

    /**
     * Sets the created time stamp for this {@link DataItem} to the specified
     * {@link String}.
     * 
     * @param timestamp
     *            A {@link String} that contains the time stamp to set.
     */
    public final void setRecordCreated(final String timestamp)
    {
        mRecordCreated = timestamp;
    }

    /**
     * Gets a {@link String} that contains the created time stamp for this
     * {@link DataItem}.
     * 
     * @return See above.
     */
    @XmlTransient
    public final String getRecordCreated()
    {
        return mRecordCreated;
    }

    /**
     * Sets the modified time stamp for this {@link DataItem} to the specified
     * {@link String}.
     * 
     * @param timestamp
     *            A {@link String} that contains the time stamp to set.
     */
    public final void setRecordModified(final String timestamp)
    {
        mRecordModified = timestamp;
    }

    /**
     * Gets a {@link String} that contains the modified time stamp for this
     * {@link DataItem}.
     * 
     * @return See above.
     */
    @XmlTransient
    public final String getRecordModified()
    {
        return mRecordModified;
    }
}
