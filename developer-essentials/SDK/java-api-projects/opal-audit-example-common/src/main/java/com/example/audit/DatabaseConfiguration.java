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

import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

/**
 * Provides access to the configuration for auditing to a database.
 */
public final class DatabaseConfiguration
{
    /** Property specifying the JNDI name of the data source to use for writing audit. */
    private static final String PROP_DATA_SOURCE_NAME = "dataSource";
    /** Property specifying the schema where audit log tables exist. */
    private static final String PROP_SCHEMA_NAME = "schema";
    /** Property specifying the table used to store quick search events. */
    private static final String PROP_QUICK_SEARCH_TABLE = "quickSearchTable";
    /** Property specifying the table used to store expand events. */
    private static final String PROP_EXPAND_TABLE = "expandTable";
    /** Property specifying the table used to store visual query events. */
    private static final String PROP_VISUAL_QUERY_TABLE = "visualQueryTable";
    /** Property specifying the table used to store record retrieval events. */
    private static final String PROP_RECORD_RETRIEVAL_TABLE = "recordRetrievalTable";

    private static final Collection<String> REQUIRED_PROPERTIES = Collections.unmodifiableCollection(Arrays.asList(
        PROP_DATA_SOURCE_NAME,
        PROP_SCHEMA_NAME,
        PROP_QUICK_SEARCH_TABLE,
        PROP_EXPAND_TABLE,
        PROP_VISUAL_QUERY_TABLE,
        PROP_RECORD_RETRIEVAL_TABLE
        ));

    private final String mDataSourceName;
    private final String mSchemaName;
    private final String mQuickSearchTableName;
    private final String mExpandTableName;
    private final String mVisualQueryTableName;
    private final String mRecordRetrievalTableName;

    /**
     * Constructor. Loads configuration from the specified properties file.
     * @param configFileName properties file name.
     * @throws RuntimeException
     *             if the configuration file is not found or required properties
     *             are not defined.
     * @throws UncheckedIOException
     *             on error reading from the configuration file.
     */
    public DatabaseConfiguration(final String configFileName)
    {
        final Properties configProps = ConfigurationLoader.loadWithRequiredProperties(configFileName, REQUIRED_PROPERTIES);

        // Get property values
        mDataSourceName = configProps.getProperty(PROP_DATA_SOURCE_NAME);
        mSchemaName = configProps.getProperty(PROP_SCHEMA_NAME);
        mQuickSearchTableName = configProps.getProperty(PROP_QUICK_SEARCH_TABLE);
        mExpandTableName = configProps.getProperty(PROP_EXPAND_TABLE);
        mVisualQueryTableName = configProps.getProperty(PROP_VISUAL_QUERY_TABLE);
        mRecordRetrievalTableName = configProps.getProperty(PROP_RECORD_RETRIEVAL_TABLE);
    }

    /**
     * Get the JNDI name of the audit data source.
     * @return JNDI name.
     */
    public String getDataSourceName()
    {
        return mDataSourceName;
    }

    /**
     * Get the database schema where are audit tables are written.
     * @return schema name.
     */
    public String getSchemaName()
    {
        return mSchemaName;
    }

    /**
     * Get the name of the table used to store Quick Search audit events.
     * @return table name.
     */
    public String getQuickSearchTableName()
    {
        return mQuickSearchTableName;
    }

    /**
     * Get the name of the table used to store Expand audit events.
     * @return table name.
     */
    public String getExpandTableName()
    {
        return mExpandTableName;
    }

    /**
     * Get the name of the table used to store Visual Query audit events.
     * @return table name.
     */
    public String getVisualQueryTableName()
    {
        return mVisualQueryTableName;
    }

    /**
     * Get the name of the table used to store Record Retrieval audit events.
     * @return table name.
     */
    public String getRecordRetrievalTableName()
    {
        return mRecordRetrievalTableName;
    }

}
