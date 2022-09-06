/*
 * MIT License
 *
 * Copyright (c) 2022, N. Harris Computer Corporation
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
