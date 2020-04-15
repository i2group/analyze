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
package com.example.audit;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Class providing a static utility method for loading configuration properties
 * files.
 */
final class ConfigurationLoader
{
    private final String mConfigFileName;
    private final Properties mConfigurationProperties;

    /**
     * Load configuration properties from the specified configuration file,
     * checking that all the required properties are defined within the
     * configuration file.
     * 
     * @param configFileName
     *            configuration file name.
     * @param requiredProperties
     *            mandatory property names.
     * @return configuration properties
     * @throws RuntimeException
     *             if the configuration file is not found or required properties
     *             are not defined.
     * @throws UncheckedIOException
     *             on error reading from the configuration file.
     */
    public static final Properties loadWithRequiredProperties(final String configFileName, final Collection<String> requiredProperties)
    {
        final ConfigurationLoader loader = new ConfigurationLoader(configFileName);
        loader.assertRequiredPropertiesPresent(requiredProperties);
        return loader.getConfigurationProperties();
    }

    /**
     * Private constructor to prevent instantiation. Only used by static utility methods.
     * @param configFileName Name of the configuration properties file.
     */
    private ConfigurationLoader(final String configFileName)
    {
        mConfigFileName = configFileName;
        mConfigurationProperties = readPropertiesFromFile();
    }

    private Properties readPropertiesFromFile()
    {
        try (final InputStream config = Thread.currentThread().getContextClassLoader().getResourceAsStream(mConfigFileName))
        {
            return readPropertiesFromStream(config);
        }
        catch (IOException ex)
        {
            throw new UncheckedIOException("Error reading configuration file: " + mConfigFileName, ex);
        }
    }

    private Properties readPropertiesFromStream(final InputStream config)
        throws IOException
    {
        if (null == config)
        {
            throw new RuntimeException("Configuration file not found: " + mConfigFileName);
        }

        final Properties configProps = new Properties();
        configProps.load(config);

        return configProps;
    }

    /**
     * Ensure that all the supplied property names are defined within the properties file.
     * @param requiredProperties Required property names.
     * @throws RuntimeException if any required properties are missing.
     */
    public void assertRequiredPropertiesPresent(final Collection<String> requiredProperties)
    {
        final Set<String> requiredPropNames = new HashSet<>(requiredProperties);
        requiredPropNames.removeAll(mConfigurationProperties.stringPropertyNames());
        if (!requiredPropNames.isEmpty())
        {
            throw new RuntimeException("Required properties not specified in configuration file " + mConfigFileName + ": " + requiredPropNames);
        }
    }

    /**
     * Get the configuration properties loaded by this object.
     * @return configuration properties.
     */
    public Properties getConfigurationProperties()
    {
        return mConfigurationProperties;
    }
}
