/*
 * MIT License
 *
 * Copyright (c) 2023, N. Harris Computer Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    public static Properties loadWithRequiredProperties(final String configFileName, final Collection<String> requiredProperties)
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
        try (InputStream config = Thread.currentThread().getContextClassLoader().getResourceAsStream(mConfigFileName))
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
