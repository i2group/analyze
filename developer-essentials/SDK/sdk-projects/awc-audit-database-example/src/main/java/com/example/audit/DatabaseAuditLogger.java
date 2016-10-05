/*
 * Copyright (c) 2014, 2016 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example.audit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.i2group.disco.audit.IAuditEvent;
import com.i2group.disco.audit.IExpandAuditEvent;
import com.i2group.disco.audit.IQuickSearchAuditEvent;
import com.i2group.disco.audit.spi.IAuditLogger;
import com.ibm.icu.text.MessageFormat;

/**
 * Audit Logger implementation that logs each type of audit event to a separate
 * database table.
 */
public final class DatabaseAuditLogger
    implements IAuditLogger
{
    /** Name of the file containing user configuration properties used by this audit logger. */
    private static final String CONFIG_FILE_NAME = "DatabaseAudit.properties";

    private static final String INSERT_SQL_COMMAND = "insert into {0}.{1} (User, Event_Time, User_Security_Groups, User_Security_Permissions, Client_User_Agent, Client_IP_Address, {2}";
    private static final String INSERT_SQL_EXPAND_VALUES = "Seeds) values (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_SQL_QUICK_SEARCH_VALUES = "Expression, Filters, Datastores) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /** SQL for inserting a Quick Search audit event into the database. */
    private final String mQuickSearchSql;
    /** SQL for inserting a Expand audit event into the database. */
    private final String mExpandSql;
    /** Data source for the audit database. */
    private final DataSource mDataSource;

    /**
     * Constructor. Initial set up of the single container-scoped audit handler
     * instance is done here. This instance is then used to handle all audit
     * events for the i2 Analyze application.
     */
    public DatabaseAuditLogger()
    {
        final DatabaseConfiguration config = new DatabaseConfiguration(CONFIG_FILE_NAME);    

        mExpandSql = MessageFormat.format(INSERT_SQL_COMMAND, config.getSchemaName(), config.getExpandTableName(), INSERT_SQL_EXPAND_VALUES);
        mQuickSearchSql = MessageFormat.format(INSERT_SQL_COMMAND, config.getSchemaName(), config.getQuickSearchTableName(), INSERT_SQL_QUICK_SEARCH_VALUES);

        mDataSource = getDataSource(config.getDataSourceName());
    }

    private static DataSource getDataSource(final String jndiName) 
    {
        try
        {
            // DataSource must be looked up explicitly. Injection using CDI will
            // not work within i2Analyze.
            final InitialContext context = new InitialContext();
            return (DataSource)context.lookup(jndiName);
        }
        catch (NamingException ex)
        {
            throw new RuntimeException("Failed to lookup data source: " + jndiName, ex);
        }
    }

    @Override
    public boolean isQueryAuditEnabled()
    {
        return true;
    }

    @Override
    public void logQuickSearch(final IQuickSearchAuditEvent event) throws RuntimeException
    {
        try (final Connection connection = mDataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(mQuickSearchSql))
        {
            setCommonParameters(statement, event);
            statement.setString(7, event.getExpression());
            statement.setString(8, event.getFilters());
            statement.setString(9, event.getDataStores().stream().collect(Collectors.joining(", ")));
            statement.executeUpdate();
        } 
        catch (SQLException ex) 
        {
            throw new RuntimeException("Failed to log quick search audit event", ex);
        }
        
    }

    @Override
    public void logExpand(final IExpandAuditEvent event) throws RuntimeException
    {
        try (final Connection connection = mDataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(mExpandSql))
        {
            setCommonParameters(statement, event);
            statement.setString(7, event.getSeeds().entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                    .collect(Collectors.joining(", ")));
            statement.executeUpdate();
        } 
        catch (SQLException ex) 
        {
            throw new RuntimeException("Failed to log expand audit event", ex);
        }
    }

    /**
     * Set parameters on the {@link PreparedStatement} that are common to all audit events.
     * @param statement an insert prepared statement.
     * @param event an audit event.
     * @throws SQLException if an error occurs setting the parameters.
     */
    private void setCommonParameters(final PreparedStatement statement, final IAuditEvent event)
        throws SQLException
    {
        statement.setString(1, event.getUser());
        statement.setTimestamp(2, Timestamp.from(event.getTimestamp()));
        statement.setString(3, event.getUserSecurityGroups().stream().collect(Collectors.joining(", ")));
        statement.setString(4, event.getUserSecurityPermissions().toString());
        statement.setString(5, event.getClientUserAgent());
        statement.setString(6, event.getClientIPAddress());
    }
}