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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.i2group.apollo.externaldata.connector.IExternalDataItemEditor;
import com.i2group.apollo.externaldata.loader.ElpStageLoader;
import com.i2group.apollo.externaldata.loader.IDataLoader;
import com.i2group.utils.ResourceHelper;
import com.i2group.utils.exception.WrappedCheckedException;

/**
 * A data loader that converts XML from an external file to platform-compatible
 * format, and uses an {@link ElpStageLoader} to manipulate items in an ELP
 * stage.
 */
public final class ExampleDelpsLoader
{
    private static final String LAW_ENFORCEMENT_XSLT_FILE_NAME = "dataToIAP.xslt";

    private static final String LOAD_ITEMS_XML_FILE_NAME = "data1.xml";
    private static final String ADDED_ITEMS_XML_FILE_NAME = "data2.xml";
    private static final String UPDATED_ITEMS_XML_FILE_NAME = "UpdatedItemsData.xml";
    private static final String ITEMS_TO_DELETE_FILE_NAME = "ItemsToDelete.properties";
    private static final String ITEMS_TO_PURGE_FILE_NAME = "ItemsToPurge.properties";

    private final IDataLoader mElpStageDataLoader;
    private ExampleItemVersionStore mExampleItemVersionStore;

    /**
     * Constructs a new {@link ExampleDelpsLoader}.
     */
    public ExampleDelpsLoader()
    {
        mElpStageDataLoader = new ElpStageLoader();
        mExampleItemVersionStore = new ExampleItemVersionStore();
    }

    /**
     * Transforms the XML file identified in LOAD_ITEMS_XML_FILE_NAME to
     * platform-compatible format, and then loads all the items in that data
     * into an ELP stage.
     * <p>
     * This example makes a single call to createItems(), passing all the XML at
     * the same time. This approach is reasonable for small data volumes, but
     * you should employ batching when you have large volumes of data.
     */
    public void load()
    {
        mExampleItemVersionStore.clear();

        final Source loadItemsXmlSource = createTransformedXmlSource(LOAD_ITEMS_XML_FILE_NAME);

        final IExternalDataItemEditor itemEditor = createItemEditor();

        final Map<String, Long> createdItemVersions = mElpStageDataLoader.createItems(loadItemsXmlSource, itemEditor);
        mExampleItemVersionStore.updateItemVersions(createdItemVersions);

        mExampleItemVersionStore.store();

        mElpStageDataLoader.finish();
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
     * In order to update (or delete or purge) an item in an ELP stage, the
     * supplied XML must contain the <em>current</em> version number of the
     * item. To determine that number, this example uses a custom item version
     * store. Calls to data loader methods are wrapped in calls to the item
     * version store, which persists a map of item identifiers to version
     * numbers. The version numbers can then be retrieved from other locations
     * in the example code.
     */
    public void synchronize()
    {
        mExampleItemVersionStore.load();

        final Source addedItemsXmlSource = createTransformedXmlSource(ADDED_ITEMS_XML_FILE_NAME);

        final Source updatedItemsXmlSource = createTransformedXmlSource(UPDATED_ITEMS_XML_FILE_NAME);

        final Map<String, Long> deletedItemIdsToVersionMap = getItemIdsToVersionMapFromSourceRecordIds(ITEMS_TO_DELETE_FILE_NAME);

        final IExternalDataItemEditor itemEditor = createItemEditor();

        final Map<String, Long> createdItemVersions = mElpStageDataLoader.createItems(addedItemsXmlSource, itemEditor);
        mExampleItemVersionStore.updateItemVersions(createdItemVersions);

        final Map<String, Long> updatedItemVersions = mElpStageDataLoader.updateItems(updatedItemsXmlSource, itemEditor);
        mExampleItemVersionStore.updateItemVersions(updatedItemVersions);

        final Map<String, Long> deletedItemVersions = mElpStageDataLoader.deleteItems(deletedItemIdsToVersionMap);
        mExampleItemVersionStore.updateItemVersions(deletedItemVersions);

        mElpStageDataLoader.finish();

        mExampleItemVersionStore.store();
    }

    /**
     * Purges the items in the file identified in ITEMS_TO_PURGE_FILE_NAME from
     * the ELP stage.
     */
    public void purge()
    {
        mExampleItemVersionStore.load();

        final Map<String, Long> purgeItemIdsToVersionMap = getItemIdsToVersionMapFromSourceRecordIds(ITEMS_TO_PURGE_FILE_NAME);

        final Map<String, Long> purgedItemVersions = mElpStageDataLoader.purgeItems(purgeItemIdsToVersionMap);
        mExampleItemVersionStore.updateItemVersions(purgedItemVersions);

        mElpStageDataLoader.finish();

        mExampleItemVersionStore.store();
    }

    /**
     * Creates and returns the item editor that the {@link ElpStageLoader} will
     * use as it processes each item.
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

        transformedXml = changeRecordVersions(transformedXml);

        final StringReader stringReader = new StringReader(transformedXml);
        return new StreamSource(stringReader);
    }

    /**
     * Adds version numbers from the item version store to the items that are
     * defined in the specified platform-compatible XML.
     * 
     * @param transformedXml
     *            A {@link String} that contains platform-compatible XML.
     * @return A {@link String} that contains the specified platform-compatible
     *         XML with added version numbers for each item.
     */
    private String changeRecordVersions(
            final String transformedXml)
    {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();

        try
        {
            final DocumentBuilder documentBuilder = documentBuilderFactory
                    .newDocumentBuilder();

            final InputSource xmlInputSource = new InputSource(
                    new StringReader((transformedXml)));

            final Document document = documentBuilder.parse(xmlInputSource);

            document.getDocumentElement().normalize();

            addVersionNumbers(document);

            return buildXmlStringFromDocument(document);
        }
        catch (ParserConfigurationException ex)
        {
            throw new WrappedCheckedException(ex);
        }
        catch (SAXException ex)
        {
            throw new WrappedCheckedException(ex);
        }
        catch (IOException ex)
        {
            throw new WrappedCheckedException(ex);
        }
    }

    /**
     * Adds elements that contain version numbers from the item version store as
     * children of particular elements in the specified XML {@link Document}.
     * 
     * @param document
     *            The XML {@link Document} to which this method adds version
     *            numbers.
     */
    private void addVersionNumbers(final Document document)
    {
        final NodeList idNodes = document.getElementsByTagName("_1_ItemId");

        for (int i = 0; i < idNodes.getLength(); i++)
        {
            final Node idNode = idNodes.item(i);

            final Long versionNumber = mExampleItemVersionStore
                    .getItemVersion(idNode
                            .getTextContent());

            if (versionNumber != null)
            {
                // Add the required 'item version' node.
                Node itemVersionNode = document
                        .createElement("_1_ItemVersion");

                itemVersionNode
                        .setTextContent(versionNumber.toString());

                idNode.getParentNode().appendChild(itemVersionNode);
            }
        }
    }

    /**
     * Converts the record identifiers in the specified file to a map that you
     * can pass to the deleteItems() or purgeItems() methods of
     * {@link ElpStageLoader}, returning the result.
     * 
     * @param sourceRecordIdsFileName
     *            A {@link String} that contains the path of the file that
     *            contains the record identifiers to be converted.
     * @return See above.
     */
    private Map<String, Long> getItemIdsToVersionMapFromSourceRecordIds(
            final String sourceRecordIdsFileName)
    {
        final Properties itemIdsProps = ResourceHelper
                .loadPropertiesResource(sourceRecordIdsFileName);
        final Map<String, Long> itemIdsMap = new HashMap<String, Long>();

        final String property = itemIdsProps.getProperty("itemIds");
        final String[] itemIdentifiers = property.split(",");

        for (String itemIdentifier : itemIdentifiers)
        {
            final String id = itemIdentifier.trim();
            final Long version = mExampleItemVersionStore
                    .getItemVersion(id);
            itemIdsMap.put(id, version);
        }

        return itemIdsMap;
    }

    /**
     * Converts the specified XML {@link Document} to a {@link String},
     * returning the result.
     * 
     * @param document
     *            The XML {@link Document} to be converted.
     * @return See above.
     */
    private static String buildXmlStringFromDocument(final Document document)
    {
        try
        {
            final StringWriter stringWriter = new StringWriter();

            final Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();

            transformer
                    .setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(document), new StreamResult(
                    stringWriter));

            final String xmlString = stringWriter.toString();

            return xmlString;
        }
        catch (TransformerConfigurationException ex)
        {
            throw new WrappedCheckedException(ex);
        }
        catch (TransformerException ex)
        {
            throw new WrappedCheckedException(ex);
        }
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
        final String queryResultXml = ExampleXmlTransformer
                .transformSourceSystemXml(LAW_ENFORCEMENT_XSLT_FILE_NAME,
                        xmlFileName);

        return queryResultXml;
    }
}
