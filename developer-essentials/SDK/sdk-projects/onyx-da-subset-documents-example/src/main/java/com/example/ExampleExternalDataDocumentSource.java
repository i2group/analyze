/*
 * Copyright (c) 2014, 2020 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import com.i2group.apollo.documentstore.DocumentNotFoundException;
import com.i2group.apollo.externaldata.documentstore.IExternalDataDocumentSource;

/**
 * An example implementation of {@link IExternalDataDocumentSource} that
 * generates an image containing the requested document identifier, for test
 * purposes.
 *
 * @since 6.0
 */
public final class ExampleExternalDataDocumentSource implements IExternalDataDocumentSource
{
    /**
     * Gets an {@link InputStream} that contains a dynamically generated image.
     * This implementation adds its parameter and a time stamp to the image that
     * it generates.
     * 
     * @param id
     *            A {@link String} that contains the identifier of the document
     *            to be downloaded.
     * @throws DocumentNotFoundException
     *            If there is no document with the specified identifier.
     * @return See above.
     */
    @Override
    public InputStream getDocumentContents(final String id) throws DocumentNotFoundException
    {
        // For the purposes of this example, we will generate an image and
        // return an InputStream with its data. In practice, a call could be
        // made to fetch the document from a database, or another source,
        // based on the ID.
        try
        {
            return generateImage(id, new Date());
        }
        catch (IOException ex)
        {
            // This exception is used to illustrate what would happen if an
            // image could not be loaded for any reason.
            throw new DocumentNotFoundException(ex);
        }
    }

    /**
     * Generates an image that contains the specified identifier and the
     * specified date, as strings.
     * 
     * @param id
     *            A {@link String} that contains the identifier to display.
     * @param date
     *            A {@link Date} that contains the date to display.
     *
     * @return An {@link InputStream} that contains the generated image data.
     * @throws IOException If the image could not be generated.
     */
    private InputStream generateImage(final String id, final Date date) throws IOException
    {
        // Attributes for image
        final int size = 256;
        final int xTextOffset = 2;
        final int yTextOffSet = 30;

        // Generate the image
        final BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        final Graphics graphics = bufferedImage.getGraphics();
        graphics.drawString(date.toString(), xTextOffset, yTextOffSet);
        graphics.drawString("id: " + id, xTextOffset, yTextOffSet * 2);
        // SUPPRESS: MagicNumber
        graphics.drawString("(DAOD Example)", xTextOffset, yTextOffSet * 3);

        // Write the BufferedImage's data into an output stream, which can then
        // be used to create an input stream.
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);

        // Return the input stream representing the image.
        final InputStream imageInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return imageInputStream;
    }
}
