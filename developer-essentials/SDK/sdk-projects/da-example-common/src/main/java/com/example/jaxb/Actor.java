/*
 * Copyright (c) 2014, 2016 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * A JAXB represention of the Actor element in the sample XML data.
 */
@XmlType(name = "Actor")
@XmlRootElement(name = "Actor")
public final class Actor extends DataItem
{
    @XmlElement(name = "FirstName", required = true)
    private String mFirstName;

    @XmlElement(name = "LastName", required = true)
    private String mLastName;

    @XmlElement(name = "DOB", required = true)
    private String mDOB;

    /**
     * Constructs a new {@link Actor}. This constructor is intended for use by
     * JAXB and should not be called by other code.
     * 
     * @exclude
     */
    protected Actor()
    {
    }

    /**
     * Sets the first name of this {@link Actor} to the specified
     * {@link String}.
     * 
     * @param firstName
     *            A {@link String} that contains the first name to set.
     */
    public void setFirstName(final String firstName)
    {
        mFirstName = firstName;
    }

    /**
     * Gets a {@link String} that contains the first name of this {@link Actor}.
     * 
     * @return See above.
     */
    @XmlTransient
    public String getFirstName()
    {
        return mFirstName;
    }

    /**
     * Sets the last name of this {@link Actor} to the specified
     * {@link String}.
     * 
     * @param lastName
     *            A {@link String} that contains the last name to set.
     */
    public void setLastName(final String lastName)
    {
        mLastName = lastName;
    }

    /**
     * Gets a {@link String} that contains the last name of this {@link Actor}.
     * 
     * @return See above.
     */
    @XmlTransient
    public String getLastName()
    {
        return mLastName;
    }

    /**
     * Sets the date of birth of this {@link Actor} to the specified
     * {@link String}.
     * 
     * @param dob
     *            A {@link String} that contains the date of birth to set.
     */
    public void setDOB(final String dob)
    {
        mDOB = dob;
    }

    /**
     * Gets a {@link String} that contains the date of birth of this
     * {@link Actor}.
     * 
     * @return See above.
     */
    @XmlTransient
    public String getDOB()
    {
        return mDOB;
    }
}
