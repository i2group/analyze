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

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.i2group.apollo.externaldata.connector.IExternalDataItemEditor;
import com.i2group.apollo.externaldata.loader.AnalysisRepositoryLoader;
import com.i2group.apollo.externaldata.loader.IDataLoader;
import com.i2group.utils.ResourceHelper;

/**
 * A data loader that converts XML from an external file to platform-compatible
 * format, and uses an {@link AnalysisRepositoryLoader} to manipulate items
 * in the Analysis Repository.
 */
public final class ExampleDataLoader
{
    private static final String LAW_ENFORCEMENT_XSLT_FILE_NAME = "dataToI2analyze.xslt";

    private static final String LOAD_ITEMS_XML_FILE_NAME = "data1.xml";
    private static final String ADDED_ITEMS_XML_FILE_NAME = "data2.xml";
    private static final String UPDATED_ITEMS_XML_FILE_NAME = "UpdatedItemsData.xml";
    private static final String ITEMS_TO_DELETE_FILE_NAME = "ItemsToDelete.properties";
    private static final String ITEMS_TO_PURGE_FILE_NAME = "ItemsToPurge.properties";

    private final IDataLoader mRepositoryDataLoader;

    /**
     * Constructs a new {@link ExampleDataLoader}.
     */
    public ExampleDataLoader()
    {
        mRepositoryDataLoader = new AnalysisRepositoryLoader();
    }

    /**
     * Transforms the XML file identified in LOAD_ITEMS_XML_FILE_NAME to
     * platform-compatible format, and then loads all the items in that data
     * into the Analysis Repository.
     * <p>
     * This example makes a single call to createItems(), passing all the XML at
     * the same time. This approach is reasonable for small data volumes, but
     * you should employ batching when you have large volumes of data.
     * <p>
     * <strong>Note:</strong> This example uses XML files that contain record
     * identifiers that are derived from their source system. You cannot load
     * the same data file more than once.
     */
    public void load()
    {
        final Source loadItemsXmlSource = createTransformedXmlSource(LOAD_ITEMS_XML_FILE_NAME);
        final IExternalDataItemEditor itemEditor = createItemEditor();
        mRepositoryDataLoader.createItems(loadItemsXmlSource, itemEditor);
    }

    /**
     * Synchronizes data changes that have taken place in the source system to
     * items that have already been imported to the Analysis Repository.
     * <p>
     * In a real system, this process involves determining what has changed in
     * the source since the initial data load or the last synchronize, and then
     * applying those changes. New records are likely to have been added; some
     * records may have been updated; and some records may have been deleted.
     * <p>
     * This example simply takes its lists of changes from supplied XML files.
     */
    public void synchronize()
    {
        final Source addedItemsXmlSource = createTransformedXmlSource(ADDED_ITEMS_XML_FILE_NAME);
        final Source updatedItemsXmlSource = createTransformedXmlSource(UPDATED_ITEMS_XML_FILE_NAME);
        final Map<String, Long> deletedItemGUIDsMap = getItemIdsMapFromSourceRecordIds(ITEMS_TO_DELETE_FILE_NAME);
        final IExternalDataItemEditor itemEditor = createItemEditor();

        mRepositoryDataLoader.createItems(addedItemsXmlSource, itemEditor);

        mRepositoryDataLoader.updateItems(updatedItemsXmlSource, itemEditor);

        mRepositoryDataLoader.deleteItems(deletedItemGUIDsMap);
    }

    /**
     * Purges the items in the file identified in ITEMS_TO_PURGE_FILE_NAME from
     * the Analysis Repository.
     */
    public void purge()
    {
        final Map<String, Long> purgeItemGUIDsMap = getItemIdsMapFromSourceRecordIds(ITEMS_TO_PURGE_FILE_NAME);

        mRepositoryDataLoader.purgeItems(purgeItemGUIDsMap);
    }

    /**
     * Creates and returns the item editor that the
     * {@link AnalysisRepositoryLoader} will use as it processes each item.
     * <p>
     * In practice, the item editor does not need to do anything. This example
     * just illustrates how the item editor could be used.
     * 
     * @return See above.
     */
    private IExternalDataItemEditor createItemEditor()
    {
        return new ExampleItemEditor();
    }

    /**
     * Transforms the XML in the specified file to platform-compatible
     * format, returning a {@link Source} that contains the result.
     * 
     * @param xmlFileName
     *            A {@link String} that contains the path of the file that
     *            contains the XML data to be transformed.
     * @return See above.
     */
    private Source createTransformedXmlSource(final String xmlFileName)
    {
        String transformedXml = transformSourceSystemXml(xmlFileName);

        final StringReader stringReader = new StringReader(transformedXml);
        return new StreamSource(stringReader);
    }

    /**
     * Converts the record identifiers in the specified file to a map that you
     * can pass to the deleteItems() or purgeItems() methods of
     * {@link AnalysisRepositoryLoader}, returning the result.
     * 
     * @param sourceRecordIdsFileName
     *            A {@link String} that contains the path of the file that
     *            contains the record identifiers to be converted.
     * @return See above.
     */
    private Map<String, Long> getItemIdsMapFromSourceRecordIds(final String sourceRecordIdsFileName)
    {
        final Properties itemIdsProps = ResourceHelper
                .loadPropertiesResource(sourceRecordIdsFileName);
        final Map<String, Long> itemIdsMap = new HashMap<String, Long>();

        final String property = itemIdsProps.getProperty("itemIds");
        final String[] itemIdentifiers = property.split(",");

        for (String itemIdentifier : itemIdentifiers)
        {
            final String id = itemIdentifier.trim();
            itemIdsMap.put(id, null);
        }

        return itemIdsMap;
    }

    /**
     * Transforms the XML in the specified file to platform-compatible
     * format, returning a {@link String} that contains the result.
     * 
     * @param xmlFileName
     *            A {@link String} that contains the path of the file that
     *            contains the XML data to be transformed.
     * @return See above.
     */
    private static String transformSourceSystemXml(final String xmlFileName)
    {
        final String queryResultXml = ExampleXmlTransformer.transformSourceSystemXml(LAW_ENFORCEMENT_XSLT_FILE_NAME,
            xmlFileName);

        return queryResultXml;
    }
}
