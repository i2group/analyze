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
package com.example;

import java.io.StringReader;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.i2group.apollo.externaldata.adapter.IExternalDataAdapter;
import com.i2group.apollo.networksearchservice.transport.NetworkSearchNodeList;
import com.i2group.apollo.searchservice.transport.FilterInfoList;
import com.i2group.apollo.searchservice.transport.ItemTypeIdList;
import com.i2group.apollo.searchservice.transport.SearchNode;
import com.i2group.apollo.searchservice.transport.SetInfo;

/**
 * An external data adapter that supports text searches and filtered searches
 * against information in an external XML file.
 */
public final class ExampleDataAdapterSubset implements IExternalDataAdapter
{
    private static final String XSLT_FILE_NAME = "dataToI2analyze.xslt";
    private static final String DATA_FILE_NAME = "data1.xml";
    private static final String NOT_SUPPORTED = "This operation is not supported";

    @Override
    public Source fullTextSearch(final String searchString)
    {
        // query external data with search term and return result.
        // In this case simplified case just returning the filename of the
        // resultant data file.
        final String dataFromExternalDataProvider = getDataFromExternalDataProvider(searchString);
        // convert external data results into i2 Analyze compatible source required.
        final Source i2analyzeFormatSource = transformToPlatformCompatibleXML(dataFromExternalDataProvider);

        return i2analyzeFormatSource;
    }

    /*
     * Not implemented in this example.
     */
    @Override
    public Source dumbbellSearch(final SearchNode fromEntity,
            final SearchNode link, final SearchNode toEntity)
    {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    /*
     * Not implemented in this example.
     */
    @Override
    public Source expand(final List<String> seedItemKeys,
            final List<String> throughLinkTypes,
            final List<String> toEntityTypes, final int maxNumberOfLinks,
            final boolean shouldThrowIfTooManyLinks)
    {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    /*
     * Not implemented in this example.
     */
    @Override
    public Source networkSearch(
            final NetworkSearchNodeList networkSearchCriteria)
    {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public Source search(final String searchString, final SetInfo setInfo,
            final ItemTypeIdList itemTypeIds, final FilterInfoList filterInfos)
    {
        // query external data with search term and return result.
        // In this case simplified case just returning the filename of the
        // resultant data file.
        final String dataFromExternalDataProvider = getDataFromExternalDataProvider(searchString);
        // convert external data results into i2 Analyze compatible source required.
        final Source i2analyzeFormatSource = transformToPlatformCompatibleXML(dataFromExternalDataProvider);

        return i2analyzeFormatSource;
    }

    /**
     * Gets a {@link String} that contains data from an external data
     * provider, given a {@link String} that identifies that provider.
     * <p>
     * This example implementation actually returns the path of an XML data
     * file in response to all requests.
     * 
     * @param searchString
     *            A {@link String} that identifies the external data provider.
     * @return A {@link String} that contains the path of an XML data file that
     *         has been created by the data provider.
     */
    private String getDataFromExternalDataProvider(final String searchString)
    {
        return DATA_FILE_NAME;
    }

    /**
     * Transforms the XML data in the specified file into platform-compatible
     * XML.
     * 
     * @param externalDataFilename
     *            A {@link String} that contains the path of the external data
     *            file to be transformed.
     * @return A {@link Source} that contains the platform-compatible XML data.
     */
    private Source transformToPlatformCompatibleXML(
            final String externalDataFilename)
    {
        final String transformSourceSystemXml = ExampleXmlTransformer
                .transformSourceSystemXml(XSLT_FILE_NAME, externalDataFilename);
        final StringReader stringReader = new StringReader(transformSourceSystemXml);
        final StreamSource streamSource = new StreamSource(stringReader);

        // return source version of transformed xml.
        return streamSource;
    }
}
