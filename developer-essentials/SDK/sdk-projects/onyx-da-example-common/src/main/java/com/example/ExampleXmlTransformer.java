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

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.i2group.utils.ResourceHelper;

/**
 * A wrapper around standard Java XSLT support that can transform XML files
 * using XSL.
 */
public final class ExampleXmlTransformer
{
    /**
     * Constructs a new {@link ExampleXmlTransformer}.
     */
    private ExampleXmlTransformer()
    {
    }

    /**
     * Transforms the XML in the specified file using the XSL transform in the
     * specified file.
     * 
     * @param xsltFileName
     *            A {@link String} that contains the path of the XSL transform
     *            file.
     * @param sourceSystemXmlFileName
     *            A {@link String} that contains the path of the XML file.
     * @return A {@link String} that contains the transformed XML.
     */
    public static String transformSourceSystemXml(final String xsltFileName,
            final String sourceSystemXmlFileName)
    {
        final InputStream sourceSystemXmlInputStream = ResourceHelper
                .getResourceAsStream(sourceSystemXmlFileName);
        final Source xmlSource = createSource(sourceSystemXmlInputStream);
        final InputStream xsltStream = ResourceHelper
                .getResourceAsStream(xsltFileName);
        final Source xsltSource = createSource(xsltStream);

        final StringWriter output = new StringWriter();

        final Result result = new StreamResult(output);

        final TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory
                .newInstance();

        try
        {
            final Transformer transformer = transformerFactory
                    .newTransformer(xsltSource);

            transformer.setParameter("filename", sourceSystemXmlFileName);
            transformer.transform(xmlSource, result);

            return output.toString();
        }
        catch (TransformerConfigurationException ex)
        {
            throw new RuntimeException(ex);
        }
        catch (TransformerException ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            try
            {
                xsltStream.close();
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
            try
            {
                sourceSystemXmlInputStream.close();
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Converts an {@link InputStream} to a {@link Source}.
     * 
     * @param inputStream
     *            The {@link InputStream} to convert.
     * @return See above.
     */
    private static Source createSource(final InputStream inputStream)
    {
        return new StreamSource(inputStream);
    }
}
