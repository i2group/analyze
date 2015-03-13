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
package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import com.i2group.apollo.externaldata.delpsservice.ElpStageUnavailableException;
import com.i2group.apollo.service.common.ItemNotFoundException;
import com.i2group.apollo.service.common.ValidationException;
import com.i2group.utils.ResourceHelper;

/**
 * A container for the entry point to the "data load ELP stage" example project.
 */
public final class ExampleDelpsLoaderMain
{
    private static final String LOAD_ARG = "-load";
    private static final String SYNCHRONIZE_ARG = "-sync";
    private static final String PURGE_ARG = "-purge";
    private static final String EMPTY_STRING = "";
    private static final String DATA_LOADER_URL_PROPERTY_NAME = "DataLoaderURL";
    private static final String DATA_LOADER_USER_NAME_PROPERTY_NAME = "DataLoaderUserName";
    private static final String DATA_LOADER_DATA_SOURCE_ID_PROPERTY_NAME = "DataLoaderDataSourceId";
    private static final String DATA_LOADER_USER_PASSWORD_PROPERTY_NAME = "DataLoaderUserPassword";
    private static final String THE_PROPERTY_HAS_NOT_BEEN_SET_MESSAGE = "The ''{0}'' property has not been set.";

    private static String sDataLoaderURL;
    private static String sDataLoaderUserName;
    private static String sDataLoaderPassword;
    private static String sDataLoaderDataSourceId;
    private static Properties sLoaderProperties;

    /**
     * Constructs a new {@link ExampleDelpsLoaderMain}.
     */
    private ExampleDelpsLoaderMain()
    {
    }

    /**
     * Main entry point.
     * 
     * @param args
     *            {@link String}s containing the command-line arguments for the
     *            example loader.
     * @throws ValidationException
     *            If a method in {@link ExampleDelpsLoader} throws a
     *            {@link ValidationException}.
     * @throws ElpStageUnavailableException
     *            If a method in {@link ExampleDelpsLoader} throws an
     *            {@link ElpStageUnavailableException}.
     * @throws ItemNotFoundException
     *            If a method in {@link ExampleDelpsLoader} throws an
     *            {@link ItemNotFoundException}.
     * @throws IOException
     *            If there is a problem loading the loader.properties file.
     */
    public static void main(final String[] args) throws ElpStageUnavailableException,
        ValidationException, ItemNotFoundException, IOException
    {
        final String help = MessageFormat.format(
                "Please supply one of: {0}, {1}, {2}", LOAD_ARG,
                SYNCHRONIZE_ARG, PURGE_ARG);

        if (args == null || args.length != 1)
        {
            System.out.println(help);
            return;
        }

        //System.setProperty("http.proxyHost", "127.0.0.1");
        //System.setProperty("http.proxyPort", "8888");

        final String option = args[0].toLowerCase();

        loadProperties();

        final ExampleDelpsLoader exampleDelpsLoader = new ExampleDelpsLoader(
                sDataLoaderURL,
                sDataLoaderUserName,
                sDataLoaderPassword,
                sDataLoaderDataSourceId);

        if (LOAD_ARG.equals(option))
        {
            System.out.println("Invoking 'load'...");
            exampleDelpsLoader.load();
            System.out.println("...'load' completed.");
            return;
        }

        if (SYNCHRONIZE_ARG.equals(option))
        {
            System.out.println("Invoking 'synchronize'...");
            exampleDelpsLoader.synchronize();
            System.out.println("...'synchronize' completed.");
            return;
        }

        if (PURGE_ARG.equals(option))
        {
            System.out.println("Invoking 'purge'...");
            exampleDelpsLoader.purge();
            System.out.println("...'purge' completed.");
            return;
        }

        System.out.println(help);
    }

    /**
     * Loads the connection parameters that {@link ExampleDelpsLoader} requires
     * from the loader.properties file.
     * 
     * @throws IOException
     *            If there is a problem loading the loader.properties file.
     */
    private static void loadProperties() throws IOException
    {
        sLoaderProperties = new Properties();
        final InputStream inputStream = ResourceHelper.getResourceAsStream("loader.properties");
        sLoaderProperties.load(inputStream);
        inputStream.close();

        sDataLoaderURL = sLoaderProperties.getProperty(DATA_LOADER_URL_PROPERTY_NAME, EMPTY_STRING);
        sDataLoaderUserName = sLoaderProperties.getProperty(DATA_LOADER_USER_NAME_PROPERTY_NAME, EMPTY_STRING);
        sDataLoaderPassword = sLoaderProperties.getProperty(DATA_LOADER_USER_PASSWORD_PROPERTY_NAME, EMPTY_STRING);
        sDataLoaderDataSourceId = sLoaderProperties.getProperty(DATA_LOADER_DATA_SOURCE_ID_PROPERTY_NAME, EMPTY_STRING);

        throwIfPropertyMissing(sDataLoaderURL, DATA_LOADER_URL_PROPERTY_NAME);
        throwIfPropertyMissing(sDataLoaderUserName, DATA_LOADER_USER_NAME_PROPERTY_NAME);
        throwIfPropertyMissing(sDataLoaderPassword, DATA_LOADER_USER_PASSWORD_PROPERTY_NAME);
        throwIfPropertyMissing(sDataLoaderDataSourceId, DATA_LOADER_DATA_SOURCE_ID_PROPERTY_NAME);
    }

    /**
     * Tests that a specified property has been set through the
     * loader.properties file, and throws an {@link IllegalArgumentException}
     * if it has not.
     * 
     * @param propertyValue
     *            A {@link String} that contains the current value of the
     *            property to be tested. If the property has not been set,
     *            then the {@link String} will be empty.
     * @param propertyName
     *            A {@link String} that contains the name of the property
     *            to be tested.
     */
    private static void throwIfPropertyMissing(final String propertyValue, final String propertyName)
    {
        if (EMPTY_STRING.equals(propertyValue))
        {
            final String errorMessage = MessageFormat.format(
            THE_PROPERTY_HAS_NOT_BEEN_SET_MESSAGE, propertyName);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
