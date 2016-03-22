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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * A JAXB representation of the Data element in the sample XML data.
 */
@XmlType(name = "Data")
@XmlRootElement(name = "Data")
public final class Data
{
    @XmlElementWrapper(name = "Actors", required = true)
    @XmlElement(name = "Actor", required = false)
    private Collection<Actor> mActors;

    @XmlElementWrapper(name = "Associations", required = true)
    @XmlElement(name = "Association", required = false)
    private Collection<Association> mAssociations;

    /**
     * Constructs a new {@link Data}. This constructor is intended for use by
     * JAXB and should not be called by other code.
     * 
     * @exclude
     */
    protected Data()
    {
    }

    /**
     * Sets {@link Actor}s in this {@link Data} to those specified.
     * 
     * @param actors
     *            The {@link Actor}s to set.
     */
    public void setActors(final Collection<Actor> actors)
    {
        mActors = actors;
    }

    /**
     * Gets the {@link Actor}s in this {@link Data}.
     * 
     * @return See above.
     */
    @XmlTransient
    public Collection<Actor> getActors()
    {
        return mActors;
    }

    /**
     * Sets the {@link Association}s in this {@link Data} to those specified.
     * 
     * @param associations
     *            The {@link Association}s to set.
     */
    public void setAssociations(final Collection<Association> associations)
    {
        mAssociations = associations;
    }

    /**
     * Gets the {@link Association}s in this {@link Data}.
     * 
     * @return See above.
     */
    @XmlTransient
    public Collection<Association> getAssociations()
    {
        return mAssociations;
    }

    /**
     * Gets all the {@link DataItem}s in this {@link Data}.
     * 
     * @return See above.
     */
    @XmlTransient
    public Collection<DataItem> getDataItems()
    {
        final List<DataItem> dataItems = new ArrayList<DataItem>();
        dataItems.addAll(mActors);
        dataItems.addAll(mAssociations);
        return dataItems;
    }
}
