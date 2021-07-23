/*
 * Copyright (c) 2014, 2021 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example.audit;

import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

/**
 * Provides access to the configuration for auditing to a file.
 */
public final class FileConfiguration
{
    /** Property specifying the log file name. */
    private static final String PROP_FILE_NAME = "fileName";
    /** Property specifying the log file size limit before log rotation occurs. */
    private static final String PROP_SIZE_LIMIT = "sizeLimit";
    /** Property specifying the number of files to use for log rotation. */
    private static final String PROP_FILE_COUNT = "fileCount";

    private static final String DEFAULT_SIZE_LIMIT = "0";
    private static final String DEFAULT_FILE_COUNT = "1";

    private static final Collection<String> REQUIRED_PROPERTIES = Collections.unmodifiableCollection(Arrays.asList(
            PROP_FILE_NAME
            ));

    private final String mFileName;
    private final int mSizeLimit;
    private final int mFileCount;

    /**
     * Constructor. Reads configuration for file writing from the specified properties file.
     * @param configFileName The configuration filename.
     * @throws RuntimeException
     *             if the configuration file is not found or required properties
     *             are not defined.
     * @throws UncheckedIOException
     *             on error reading from the configuration file.
     */
    public FileConfiguration(final String configFileName)
    {
        final Properties configProps = ConfigurationLoader.loadWithRequiredProperties(configFileName, REQUIRED_PROPERTIES);

        // Get property values
        mFileName = configProps.getProperty(PROP_FILE_NAME);
        mSizeLimit = Integer.parseInt(configProps.getProperty(PROP_SIZE_LIMIT, DEFAULT_SIZE_LIMIT));
        mFileCount = Integer.parseInt(configProps.getProperty(PROP_FILE_COUNT, DEFAULT_FILE_COUNT));
    }

    /**
     * Get the name of the audit log file.
     * @return file name.
     */
    public String getFileName()
    {
        return mFileName;
    }

    /**
     * Get the audit log file size limit before log rotation occurs.
     * @return log file size limit.
     */
    public int getSizeLimit()
    {
        return mSizeLimit;
    }

    /**
     * Get the number of files to use for log rotation.
     * @return number of files.
     */
    public int getFileCount()
    {
        return mFileCount;
    }
}
