/*
 * Copyright (c) 2014, 2015 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.example.ExampleXmlTransformer;
import com.example.SubsetToken;
import com.example.SubsetTokenMarshaller;
import com.i2group.apollo.externaldata.subsetexplorationservice.IExternalDataSubsetExplorationService;
import com.i2group.apollo.externaldata.subsetexplorationservice.sdk.ExternalDataSubsetExplorationServiceFactory;
import com.i2group.apollo.externaldata.transport.ExternalDataSubsetIdentifier;
import com.i2group.apollo.rest.WebServiceContextAdapter;
import com.i2group.utils.exception.WrappedCheckedException;

/**
 * Implements {@link IExternalDataSubsetResource}.
 */
@Path("/daodSubsets")
public class ExternalDataSubsetResource implements IExternalDataSubsetResource
{
    private static final String XSLT_FILE_NAME = "dataToI2analyze.xslt";
    private static final String DATA_FILE_1_NAME = "data1.xml";
    private static final String DATA_1_SEARCH_TERM = "data1";
    private static final String DATA_FILE_2_NAME = "data2.xml";
    private static final String DATA_2_SEARCH_TERM = "data2";
    private static final String EMPTY_DATA_FILE_NAME = "emptyData.xml";

    @Context
    private ServletContext mServletContext;
    @Context
    private HttpServletRequest mHttpServletRequest;
    @Context
    private HttpServletResponse mHttpServletResponse;
    @Context
    private UriInfo mUriInfo;

    private final ExternalDataSubsetExplorationServiceFactory mExternalDataSubsetExplorationServiceFactory;
    private final SubsetTokenMarshaller mSubsetTokenMarshaller;

    /**
     * Constructs a new {@link ExternalDataSubsetResource}.
     */
    public ExternalDataSubsetResource()
    {
        mExternalDataSubsetExplorationServiceFactory = new ExternalDataSubsetExplorationServiceFactory();
        mSubsetTokenMarshaller = new SubsetTokenMarshaller();
    }

    @Override
    public final Response createSubset(final SubsetRequest subsetRequest)
    {
        validateRequest(subsetRequest);
        final String externalDataFileName = getDataFromExternalDataProvider(subsetRequest);
        final String pathToSubset = transformToPlatformCompatibleXMLAndStoreSubset(
                externalDataFileName, subsetRequest);
        final ExternalDataSubsetIdentifier subsetIdentifier = createSubsetIdentifier(pathToSubset);
        final ExternalDataSubsetIdentifier initializedSubsetIdentifier = initializeSubset(subsetIdentifier);
        final SubsetResponse response = createResponseWithSubsetToken(initializedSubsetIdentifier);
        return Response.ok(response).build();
    }

    /**
     * Initializes the specified subset, which enables further searches to take
     * place against it.
     * 
     * @param subsetIdentifier
     *            The {@link ExternalDataSubsetIdentifier} of the subset to be
     *            initialized.
     * @return The updated {@link ExternalDataSubsetIdentifier}.
     */
    private ExternalDataSubsetIdentifier initializeSubset(
            final ExternalDataSubsetIdentifier subsetIdentifier)
    {
        final WebServiceContextAdapter webServiceContextAdapter = new WebServiceContextAdapter(
                mServletContext, mHttpServletRequest, mHttpServletResponse,
                mUriInfo);
        final IExternalDataSubsetExplorationService externalDataService = mExternalDataSubsetExplorationServiceFactory
                .createExternalDataService(webServiceContextAdapter);

        ExternalDataSubsetIdentifier newIdentifierForInitializedSubset = null;

        try
        {
            newIdentifierForInitializedSubset = externalDataService.initialize(subsetIdentifier);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throwWebApplicationExceptionAsInternalServerError(ex);
        }

        return newIdentifierForInitializedSubset;
    }

    /**
     * Validates the specified {@link SubsetRequest} by testing whether it
     * contains a search string.
     * 
     * @param subsetRequest
     *            The {@link SubsetRequest} to be validated.
     */
    private void validateRequest(final SubsetRequest subsetRequest)
    {
        if (subsetRequest.getSearchString() == null)
        {
            throwWebApplicationExceptionAsBadRequest("Invalid parameters: No search string provided");
        }
    }

    /**
     * Creates and returns a {@link SubsetResponse} that contains information
     * from the specified {@link ExternalDataSubsetIdentifier}.
     * 
     * @param subsetIdentifier
     *            The {@link ExternalDataSubsetIdentifier} with which to create
     *            the {@link SubsetResponse}.
     * @return See above.
     */
    private SubsetResponse createResponseWithSubsetToken(
            final ExternalDataSubsetIdentifier subsetIdentifier)
    {
        // The data source identifier is the context root of the application.
        final String dataSourceId = mHttpServletRequest.getContextPath()
                .substring(1);
        final SubsetResponse response = new SubsetResponse(subsetIdentifier,
                dataSourceId);
        return response;
    }

    /**
     * Creates and returns an {@link ExternalDataSubsetIdentifier} that contains
     * the specified path to an XML file that represents the subset.
     * 
     * @param pathToSubset
     *            A {@link String} that contains the path to a XML file that
     *            represents the subset.
     * @return See above.
     */
    private ExternalDataSubsetIdentifier createSubsetIdentifier(
            final String pathToSubset)
    {
        final String name = "examplesubset";
        final String key = UUID.randomUUID().toString();

        // The prefix ensures that a new subset is initialized every time
        // this REST resource is called, even with the same source file path.
        final String uniquePrefix = key;
        final SubsetToken subsetToken = new SubsetToken(uniquePrefix,
                pathToSubset);

        final String subsetTokenAsString = mSubsetTokenMarshaller
                .marshall(subsetToken);
        final ExternalDataSubsetIdentifier subsetIdentifier = new ExternalDataSubsetIdentifier(key, name);
        subsetIdentifier.setSubsetToken(subsetTokenAsString);
        return subsetIdentifier;
    }

    /**
     * Transforms the data in the specified file into platform-compatible
     * format, and saves the result to a new file.
     * 
     * @param externalDataFileName
     *            A {@link String} that contains the name of the file that has
     *            data in platform-incompatible format.
     * @param subsetRequest
     *            The {@link SubsetRequest} that contains the search string.
     * @return A {@link String} that contains the path to the new XML file that
     *         has the data in platform-compatible format.
     */
    private String transformToPlatformCompatibleXMLAndStoreSubset(
            final String externalDataFileName, final SubsetRequest subsetRequest)
    {
        final String dataSourceId = mHttpServletRequest.getContextPath()
                .substring(1);
        final String apolloDataDir = System.getProperty("APOLLO_DATA");
        final String itemsXmlPath = apolloDataDir + "/" + dataSourceId + "_" + subsetRequest.getSearchString().replace(' ', '_') + "_results.xml";
        final File outputFile = new File(itemsXmlPath);

        final String subsetXml = ExampleXmlTransformer
                .transformSourceSystemXml(XSLT_FILE_NAME, externalDataFileName);

        PrintWriter printWriter = null;
        try
        {
            printWriter = new PrintWriter(outputFile, StandardCharsets.UTF_8.name());
            printWriter.println(subsetXml);
        }
        catch (FileNotFoundException | UnsupportedEncodingException ex)
        {
            throw new WrappedCheckedException(ex);
        }
        finally
        {
            if (printWriter != null)
            {
                printWriter.close();
            }
        }

        return itemsXmlPath;
    }

    /**
     * An example of retrieving data from an external data provider, given a
     * search string.
     * <p>
     * This example just matches searches for "data1" or "data2". Any other
     * search string returns an empty result.
     * 
     * @param subsetRequest
     *            The {@link SubsetRequest} that contains the search string.
     * @return A {@link String} that contains the name of the resulting file
     *         that has data in platform-incompatible format.
     */
    private String getDataFromExternalDataProvider(
            final SubsetRequest subsetRequest)
    {
        final String searchString = subsetRequest.getSearchString();
        String externalDataFileName;

        if (searchString.equals(DATA_1_SEARCH_TERM))
        {
            externalDataFileName = DATA_FILE_1_NAME;
        }
        else if (searchString.equals(DATA_2_SEARCH_TERM))
        {
            externalDataFileName = DATA_FILE_2_NAME;
        }
        else
        {
            externalDataFileName = EMPTY_DATA_FILE_NAME;
        }
        return externalDataFileName;
    }

    /**
     * Throws a {@link WebApplicationException} that represents a bad request.
     * 
     * @param message
     *            A {@link String} that contains a message to add to the
     *            {@link Response} in the {@link WebApplicationException}.
     */
    private void throwWebApplicationExceptionAsBadRequest(final String message)
    {
        throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
                .entity(message).build());
    }

    /**
     * Throws a {@link WebApplicationException} that represents an internal
     * server error.
     * 
     * @param ex
     *            The {@link Throwable} exception whose message is added to the
     *            {@link Response} in the {@link WebApplicationException}.
     */
    private void throwWebApplicationExceptionAsInternalServerError(
            final Throwable ex)
    {
        final String errorText = ex.getClass().getName() + ":" + ex.getMessage();
        throw new WebApplicationException(Response
                .status(Status.INTERNAL_SERVER_ERROR).entity(errorText).build());
    }
}
