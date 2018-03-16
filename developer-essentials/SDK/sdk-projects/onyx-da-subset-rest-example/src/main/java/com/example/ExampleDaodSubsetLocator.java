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
package com.example;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.i2group.apollo.externaldata.connector.IExternalDataSubsetLocator;
import com.i2group.apollo.externaldata.transport.ExternalDataSubsetIdentifier;

/**
 * An implementation of {@link IExternalDataSubsetLocator} that just returns
 * the subset, which is already in platform-compatible format.
 */
public final class ExampleDaodSubsetLocator implements
        IExternalDataSubsetLocator
{
    private final SubsetTokenMarshaller mSubsetTokenMarshaller;

    /**
     * Constructs a new {@link ExampleDaodSubsetLocator}.
     */
    public ExampleDaodSubsetLocator()
    {
        mSubsetTokenMarshaller = new SubsetTokenMarshaller();
    }

    /**
     * Opens the XML file that is identified in the specified
     * {@link ExternalDataSubsetIdentifier} and returns a {@link Source} that
     * represents its contents.
     * 
     * @param subsetIdentifier
     *            An {@link ExternalDataSubsetIdentifier} that identifies the
     *            XML file to be opened.
     * @return See above.
     */
    public Source getSubset(final ExternalDataSubsetIdentifier subsetIdentifier)
    {
        final String subsetTokenString = subsetIdentifier.getSubsetToken();
        final SubsetToken token = mSubsetTokenMarshaller
                .unmarshall(subsetTokenString);
        final File subsetFile = new File(token.getPathToSubset());
        return new StreamSource(subsetFile);
    }
}
