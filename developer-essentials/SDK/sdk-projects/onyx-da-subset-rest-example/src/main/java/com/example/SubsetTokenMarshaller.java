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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.i2group.apollo.common.jaxb.XmlConvertor;

/**
 * A marshaler for the subset token.
 */
public final class SubsetTokenMarshaller
{
    private final JAXBContext mContext;

    /**
     * Constructs a new {@link SubsetTokenMarshaller}.
     */
    public SubsetTokenMarshaller()
    {
        try
        {
            mContext = JAXBContext.newInstance(SubsetToken.class);
        }
        catch (JAXBException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Unmarshals the specified XML {@link String} into a {@link SubsetToken}.
     * 
     * @param subsetTokenXmlString
     *            A {@link String} that contains the subset token in XML form.
     * @return The created {@link SubsetToken}.
     */
    public SubsetToken unmarshall(final String subsetTokenXmlString)
    {
        final XmlConvertor xmlConvertor = new XmlConvertor(mContext);
        return (SubsetToken) xmlConvertor.fromXml(subsetTokenXmlString);
    }

    /**
     * Marshals the specified {@link SubsetToken} into an XML {@link String}.
     * 
     * @param subsetToken
     *            The {@link SubsetToken} that is to be marshaled.
     * @return A {@link String} that contains the subset token in XML form.
     */
    public String marshall(final SubsetToken subsetToken)
    {
        final XmlConvertor xmlConvertor = new XmlConvertor(mContext);
        final String xml = xmlConvertor.toXml(subsetToken);
        return xml;
    }
}
