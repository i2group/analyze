/*
 * Copyright (c) 2014 IBM Corp.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.i2group.apollo.client.ClientFactoryFactory;
import com.i2group.apollo.client.IClientFactory;
import com.i2group.apollo.client.IDelpsServiceFacade;
import com.i2group.apollo.externaldata.delpsservice.ElpStageUnavailableException;
import com.i2group.apollo.service.common.ItemNotFoundException;
import com.i2group.apollo.service.common.ValidationException;
import com.i2group.utils.ResourceHelper;

/**
 * A data loader that converts XML from an external file to platform-compatible format, and uses an
 * {@link ElpStageLoader2} to manipulate items in an ELP stage.
 */
public final class ExampleDelpsLoader
{
    private static final String LAW_ENFORCEMENT_XSLT_FILE_NAME = "dataToIAP.xslt";

    private static final String LOAD_ITEMS_XML_FILE_NAME       = "data1.xml";
    private static final String ADDED_ITEMS_XML_FILE_NAME      = "data2.xml";
    private static final String UPDATED_ITEMS_XML_FILE_NAME    = "UpdatedItemsData.xml";
    private static final String ITEMS_TO_DELETE_FILE_NAME      = "ItemsToDelete.properties";
    private static final String ITEMS_TO_PURGE_FILE_NAME       = "ItemsToPurge.properties";

    private IClientFactory mClientFactory;
    private final String mDatasourceId;

    /**
     * Constructs a new {@link ExampleDelpsLoader}.
     * 
     * @param serverURL
     *            The URL of the Apollo Server. Usually:
     *            http://localhost/apollo/
     * @param username
     *            The username to connect as. e.g. Demo
     * @param password
     *            The users password. e.g. password
     * @param datasourceId
     *            The DELPS data source id.
     */
    public ExampleDelpsLoader(final String serverURL, final String username, final String password,
            final String datasourceId)
    {
        mDatasourceId = datasourceId;
        mClientFactory = ClientFactoryFactory.createClientFactory(serverURL, username, password);
    }

    /**
     * Transforms the XML file identified in LOAD_ITEMS_XML_FILE_NAME to
     * platform-compatible format, and then loads all the items in that data
     * into an ELP stage.
     * <p>
     * This example makes a single call to createItems(), passing all the XML at
     * the same time. This approach is reasonable for small data volumes, but
     * you should employ batching when you have large volumes of data.
     * 
     * @throws ElpStageUnavailableException
     *             In this example there is nothing that can be productively
     *             done in the case of a {@link ElpStageUnavailableException} so
     *             it is rethrown.
     * @throws ValidationException
     *             In this example there is nothing that can be productively
     *             done in the case of a {@link ValidationException} so it is
     *             rethrown.
     */
    public void load() throws ElpStageUnavailableException, ValidationException
    {
        final Source loadItemsXmlSource = createTransformedXmlSource(LOAD_ITEMS_XML_FILE_NAME);

        mClientFactory.createDelpsServiceClient(mDatasourceId).createOrReplaceItems(loadItemsXmlSource);
    }

    /**
     * Synchronizes data changes that have taken place in the source system to
     * items that have already been imported to the ELP stage.
     * <p>
     * In a real system, this process involves determining what has changed in
     * the source since the initial data load or the last synchronize, and then
     * applying those changes. New records are likely to have been added; some
     * records may have been updated; and some records may have been deleted.
     * <p>
     * This example simply takes its lists of changes from supplied XML files.
     * <p>
     * 
     * @throws ElpStageUnavailableException
     *             In this example there is nothing that can be productively
     *             done in the case of a {@link ElpStageUnavailableException} so
     *             it is rethrown.
     * @throws ItemNotFoundException
     *             In this example there is nothing that can be productively
     *             done in the case of a {@link ItemNotFoundException} so it is
     *             rethrown.
     * @throws ValidationException
     *             In this example there is nothing that can be productively
     *             done in the case of a {@link ValidationException} so it is
     *             rethrown.
     */
    public void synchronize() throws ElpStageUnavailableException, ItemNotFoundException,
        ValidationException
    {
        final Source addedItemsXmlSource = createTransformedXmlSource(ADDED_ITEMS_XML_FILE_NAME);

        final Source updatedItemsXmlSource = createTransformedXmlSource(UPDATED_ITEMS_XML_FILE_NAME);

        final List<String> deletedItemIds = getItemIdsFromSourceRecordIds(ITEMS_TO_DELETE_FILE_NAME);
        IDelpsServiceFacade client = mClientFactory.createDelpsServiceClient(mDatasourceId);

        client.createOrReplaceItems(addedItemsXmlSource);
        client.createOrReplaceItems(updatedItemsXmlSource);
        client.deleteItems(deletedItemIds);
    }

    /**
     * Purges the items in the file identified in ITEMS_TO_PURGE_FILE_NAME from the ELP stage.
     * 
     @throws ElpStageUnavailableException
     *             In this example there is nothing that can be productively
     *             done in the case of a {@link ElpStageUnavailableException} so
     *             it is rethrown.
     * @throws ValidationException
     *             In this example there is nothing that can be productively
     *             done in the case of a {@link InvalidArgumentException} so it
     *             is rethrown.
     * @throws ItemNotFoundException
     *             In this example there is nothing that can be productively
     *             done in the case of a {@link ItemNotFoundException} so it is
     *             rethrown.
     */
    public void purge() throws ElpStageUnavailableException, ValidationException, ItemNotFoundException
    {
        final List<String> purgeItemIds = getItemIdsFromSourceRecordIds(ITEMS_TO_PURGE_FILE_NAME);

        mClientFactory.createDelpsServiceClient(mDatasourceId).purgeItems(purgeItemIds);

    }

    /**
     * Transforms the XML in the specified file to platform-compatible format, returning a {@link Source} that contains
     * the result.
     * 
     * @param xmlFileName
     *            A {@link String} that contains the path of the file that contains the XML data to be transformed.
     * @return See above.
     */
    private Source createTransformedXmlSource(final String xmlFileName)
    {
        String transformedXml = transformSourceSystemXml(xmlFileName);

        final StringReader stringReader = new StringReader(transformedXml);
        return new StreamSource(stringReader);
    }

    /**
     * Converts the record identifiers in the specified file to a collection that you can pass to the deleteItems() or
     * purgeItems() methods of {@link ElpStageLoader2}, returning the result.
     * 
     * @param sourceRecordIdsFileName
     *            A {@link String} that contains the path of the file that contains the record identifiers to be
     *            converted.
     * @return See above.
     */
    private List<String> getItemIdsFromSourceRecordIds(final String sourceRecordIdsFileName)
    {
        final Properties itemIdsProps = ResourceHelper.loadPropertiesResource(sourceRecordIdsFileName);
        final List<String> itemIds = new ArrayList<String>();

        final String property = itemIdsProps.getProperty("itemIds");
        final String[] itemIdentifiers = property.split(",");

        for (String itemIdentifier : itemIdentifiers)
        {
            final String id = itemIdentifier.trim();
            itemIds.add(id);
        }

        return itemIds;
    }

    /**
     * Transforms the XML in the specified file to platform-compatible format, returning a {@link String} that contains
     * the result.
     * 
     * @param xmlFileName
     *            A {@link String} that contains the path of the file that contains the XML data to be transformed.
     * @return See above.
     */
    private static String transformSourceSystemXml(final String xmlFileName)
    {
        final String queryResultXml = ExampleXmlTransformer.transformSourceSystemXml(LAW_ENFORCEMENT_XSLT_FILE_NAME,
                xmlFileName);

        return queryResultXml;
    }
}
