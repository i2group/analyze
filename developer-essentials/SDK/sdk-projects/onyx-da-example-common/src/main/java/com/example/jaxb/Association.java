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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * A JAXB representation of the Association element in the sample XML data.
 */
@XmlType(name = "Association")
@XmlRootElement(name = "Association")
public final class Association extends DataItem
{
    @XmlElement(name = "Actor1", required = true)
    private String mActor1;

    @XmlElement(name = "Actor2", required = true)
    private String mActor2;

    /**
     * Constructs a new {@link Association}. This constructor is intended for
     * use by JAXB and should not be called by other code.
     * 
     * @exclude
     */
    protected Association()
    {
    }

    /**
     * Sets the first Actor in this {@link Association} to the one with the
     * specified {@link String} identifier.
     * 
     * @param actor1
     *            A {@link String} that contains the identifier of the Actor to
     *            set.
     */
    public void setActor1(final String actor1)
    {
        mActor1 = actor1;
    }

    /**
     * Gets a {@link String} that contains the identifier of the first Actor in
     * this {@link Association}.
     * 
     * @return See above.
     */
    @XmlTransient
    public String getActor1()
    {
        return mActor1;
    }

    /**
     * Sets the second Actor in this {@link Association} to the one with the
     * specified {@link String} identifier.
     * 
     * @param actor2
     *            A {@link String} that contains the identifier of the Actor to
     *            set.
     */
    public void setActor2(final String actor2)
    {
        mActor2 = actor2;
    }

    /**
     * Gets a {@link String} that contains the identifier of the second Actor in
     * this {@link Association}.
     * 
     * @return See above.
     */
    @XmlTransient
    public String getActor2()
    {
        return mActor2;
    }
}
