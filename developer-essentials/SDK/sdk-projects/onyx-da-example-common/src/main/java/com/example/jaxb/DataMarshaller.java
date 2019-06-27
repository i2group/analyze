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
package com.example.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;

import com.i2group.apollo.common.jaxb.XmlConvertor;
import com.i2group.utils.ResourceHelper;

/**
 * Marshals and unmarshals the sample XML data.
 */
public final class DataMarshaller
{
    private static final int BUFFER_SIZE = 1024;
    private JAXBContext mContext;

    /**
     * Constructs a new {@link DataMarshaller}.
     */
    public DataMarshaller()
    {
        try
        {
            mContext = JAXBContext.newInstance(Data.class, Actor.class,
                    Association.class);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Unmarshals the XML data in the specified file to a {@link Data} object.
     * 
     * @param fileName
     *            A {@link String} that contains the path to the file that
     *            contains the XML data.
     * @return A {@link Data} object that contains all the {@link DataItem}s
     *         from the XML file.
     */
    public Data unmarshallFromSourceFile(final String fileName)
    {
        final XmlConvertor xmlConvertor = new XmlConvertor(mContext);
        InputStream resourceAsStream = null;
        ByteArrayOutputStream outputStream = null;
        try
        {
            resourceAsStream = ResourceHelper.getResourceAsStream(fileName);
            outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = resourceAsStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }

            return (Data) xmlConvertor.fromXml(outputStream.toString("UTF-8"));
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            try
            {
                if (resourceAsStream != null)
                {
                    resourceAsStream.close();
                }
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
            try
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }

    }

    /**
     * Marshals the specified {@link Data} object to an XML {@link String}.
     * 
     * @param data
     *            The {@link Data} to marshal.
     * @return A {@link String} that contains the marshaled XML data.
     */
    public String marshallFromData(final Data data)
    {
        final XmlConvertor xmlConvertor = new XmlConvertor(mContext);
        final String xml = xmlConvertor.toXml(data);
        return xml;
    }
}
