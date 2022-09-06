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

package com.example.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.i2group.disco.task.spi.CustomTaskFailedException;
import com.i2group.disco.task.spi.IScheduledTask;
import com.i2group.tracing.ITracer;

/**
 * An implementation of {@link IScheduledTask} that will delete charts that have
 * not been accessed in the last 5 minutes.
 */
public final class ExampleTask implements IScheduledTask
{
    private static final String ENVIRONMENT_NAME = "java:comp/env";
    private static final String INFO_STORE_NAME = "ds/InfoStore";

    private static final String SELECT_CHART_BY_ACCESS_TIME = "SELECT %s FROM IS_PUBLIC.E_ANALYST_S_NOTEBOOK_CH_DV WHERE deleted = 'N' AND last_seen_time <= %s";
    private static final int MAX_CHART_AGE = 5;
    private static final String UNITS = "MINUTE";
    private static final String ITEM_ID = "ITEM_ID";
    private static final String P_NAME = "P_NAME";

    private DataSource dataSource;
    private ITracer tracer;
    private boolean isDbDialectDb2;

    @Override
    public void onStartup(final IScheduledTaskObjects objects)
    {
        tracer = objects.getTracerFactory().getTracer(getClass());
        dataSource = lookupDataSource();
        isDbDialectDb2 = determineDbDialect();
    }

    @Override
    public void run()
    {
        try (Connection connection = dataSource.getConnection())
        {
            // Delete unused charts that are expired.
            final String selectColumns = String.format("%s, %s", ITEM_ID, P_NAME);
            try (PreparedStatement statement = connection
                    .prepareStatement(String.format(SELECT_CHART_BY_ACCESS_TIME, selectColumns, timeSinceAccess())))
            {
                final ResultSet resultSet = statement.executeQuery();
                boolean purgeRequired = false;
                while (resultSet.next())
                {
                    final String itemId = resultSet.getString(ITEM_ID);

                    softDeleteChart(connection, itemId);
                    purgeRequired = true;
                }
                if (purgeRequired)
                {
                    final String purgeSoftDeletedRecordsProcedure = "IS_PUBLIC.PURGE_SOFT_DELETED_RECORDS";
                    if (isDbDialectDb2)
                    {
                        connection.createStatement()
                                .execute(String.format("CALL %s", purgeSoftDeletedRecordsProcedure));
                    }
                    else
                    {
                        connection.createStatement()
                                .execute(String.format("EXEC %s", purgeSoftDeletedRecordsProcedure));
                    }
                }
            }
        }
        catch (final SQLException ex)
        {
            tracer.warn("Could not create alert", ex);
            throw CustomTaskFailedException.reschedule("Failed to create alerts. Will try again later.");
        }
    }

    private String timeSinceAccess()
    {
        if (isDbDialectDb2)
        {
            return String.format("CURRENT TIMESTAMP -%s %s", MAX_CHART_AGE, UNITS);
        }
        return String.format("DATEADD(%s,-%s,GETDATE())", UNITS, MAX_CHART_AGE);
    }

    private void softDeleteChart(final Connection connection, final String itemId) throws SQLException
    {
        try (PreparedStatement delete = connection
                .prepareStatement("UPDATE IS_PUBLIC.E_ANALYST_S_NOTEBOOK_CH_DV SET deleted = 'Y' where ITEM_ID = ?"))
        {
            delete.setString(1, itemId);
            delete.execute();
        }
    }

    private DataSource lookupDataSource()
    {
        try
        {
            // DataSource must be looked up explicitly. Injection using CDI will
            // not work within i2Analyze.
            final InitialContext context = new InitialContext();
            final Context environmentContext = (Context) context.lookup(ENVIRONMENT_NAME);
            return (DataSource) environmentContext.lookup(INFO_STORE_NAME);
        }
        catch (final NamingException ex)
        {
            throw new RuntimeException("Failed to lookup data source: " + INFO_STORE_NAME, ex);
        }
    }

    private boolean determineDbDialect()
    {
        try (Connection connection = dataSource.getConnection())
        {
            final String productName = connection.getMetaData().getDatabaseProductName();
            return productName.toLowerCase().contains("db2");
        }
        catch (SQLException ex)
        {
            throw CustomTaskFailedException.cancel("Failed to determine database dialect.");
        }
    }
}
