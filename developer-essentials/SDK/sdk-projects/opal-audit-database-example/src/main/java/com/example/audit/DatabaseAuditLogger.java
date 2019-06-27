/*
 * Copyright (c) 2014, 2019 IBM Corp.
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
import com.i2group.disco.audit.IRecordRetrievalAuditEvent;
import com.i2group.disco.audit.IVisualQueryAuditEvent;
import com.i2group.disco.audit.spi.IAuditLogger;

/**
 * Audit Logger implementation that logs each type of audit event to a separate
 * database table.
 */
public final class DatabaseAuditLogger
    implements IAuditLogger
{
    private static final String INSERT_SQL_TEMPLATE = "insert into %s.%s (%s) values (%s)";
    private static final String COMMON_COLUMNS =
        "User_Name, Event_Time, User_Security_Groups, User_Security_Permissions, Client_User_Agent, Client_IP_Address";
    private static final String COMMON_VALUES = "?, ?, ?, ?, ?, ?";
    private static final String QUICK_SEARCH_COLUMNS = "Expression, Filters, Datastores";
    private static final String QUICK_SEARCH_VALUES = "?, ?, ?";
    private static final String EXPAND_COLUMNS = "Seeds";
    private static final String EXPAND_VALUES = "?";
    private static final String VISUAL_QUERY_COLUMNS = "Datastores, Query";
    private static final String VISUAL_QUERY_VALUES = "?, ?";
    private static final String RECORD_RETRIEVAL_COLUMNS = "Records";
    private static final String RECORD_RETRIEVAL_VALUES = "?";
    
    /** Configuration properties used by this audit logger. */
    private final DatabaseConfiguration mConfiguration = new DatabaseConfiguration("DatabaseAudit.properties");

    /** SQL for inserting a Quick Search audit event into the database. */
    private final String mQuickSearchSql;
    /** SQL for inserting a Expand audit event into the database. */
    private final String mExpandSql;
    /** SQL for inserting a Visual Query audit event into the database. */
    private final String mVisualQuerySql;
    /** SQL for inserting a Record Retrieval audit event into the database. */
    private final String mRecordRetrievalSql;

    /** Data source for the audit database. */
    private final DataSource mDataSource;

    /**
     * Constructor. Initial set up of the single container-scoped audit handler
     * instance is done here. This instance is then used to handle all audit
     * events for the i2 Analyze application.
     */
    public DatabaseAuditLogger()
    {
        mQuickSearchSql = buildInsertSql(
                mConfiguration.getQuickSearchTableName(),
                QUICK_SEARCH_COLUMNS,
                QUICK_SEARCH_VALUES);

        mExpandSql = buildInsertSql(
                mConfiguration.getExpandTableName(),
                EXPAND_COLUMNS,
                EXPAND_VALUES);
        
        mVisualQuerySql = buildInsertSql(
                mConfiguration.getVisualQueryTableName(),
                VISUAL_QUERY_COLUMNS,
                VISUAL_QUERY_VALUES);
        
        mRecordRetrievalSql = buildInsertSql(
                mConfiguration.getRecordRetrievalTableName(),
                RECORD_RETRIEVAL_COLUMNS,
                RECORD_RETRIEVAL_VALUES);

        mDataSource = lookupDataSource();
    }
    
    private String buildInsertSql(final String tableName, final String columns, final String values)
    {
        return String.format(INSERT_SQL_TEMPLATE,
            mConfiguration.getSchemaName(),
            tableName,
            String.join(", ", COMMON_COLUMNS, columns),
            String.join(", ", COMMON_VALUES, values));
    }
    
    private DataSource lookupDataSource() 
    {
        final String jndiName = mConfiguration.getDataSourceName();
        try
        {
            // DataSource must be looked up explicitly. Injection using CDI will
            // not work within i2Analyze.
            final InitialContext context = new InitialContext();
            return (DataSource)context.lookup(jndiName);
        }
        catch (final NamingException ex)
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
    public void logQuickSearch(final IQuickSearchAuditEvent event)
    {
        try (final Connection connection = mDataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(mQuickSearchSql))
        {
            setCommonParameters(statement, event);
            statement.setString(7, event.getExpression());
            statement.setString(8, event.getFilters());
            final String dataStoresText = event.getDataStores().stream()
                .collect(Collectors.joining(", "));
            statement.setString(9, dataStoresText);
            statement.executeUpdate();
        } 
        catch (final SQLException ex) 
        {
            throw new RuntimeException("Failed to log quick search audit event", ex);
        }
        
    }

    @Override
    public void logExpand(final IExpandAuditEvent event)
    {
        try (final Connection connection = mDataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(mExpandSql))
        {
            setCommonParameters(statement, event);
            final String seedText = event.getSeedRecords().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
            statement.setString(7, seedText);
            statement.executeUpdate();
        } 
        catch (final SQLException ex) 
        {
            throw new RuntimeException("Failed to log expand audit event", ex);
        }
    }
    
    @Override
    public void logVisualQuery(final IVisualQueryAuditEvent event)
    {
        try (final Connection connection = mDataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(mVisualQuerySql))
        {
            setCommonParameters(statement, event);
            final String dataStoresText = event.getDataStores().stream()
                .collect(Collectors.joining(", "));
            statement.setString(7, dataStoresText);
            statement.setString(8, event.getQuery());
            statement.executeUpdate();
        } 
        catch (final SQLException ex) 
        {
            throw new RuntimeException("Failed to log visual query audit event", ex);
        }
    }
    
    @Override
    public boolean isRecordRetrievalAuditEnabled()
    {
        return true;
    }
    
    @Override
    public void logRecordRetrieval(final IRecordRetrievalAuditEvent event)
    {
        try (final Connection connection = mDataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(mRecordRetrievalSql))
        {
            setCommonParameters(statement, event);
            final String recordsText = event.getRecords().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
            statement.setString(7, recordsText);
            statement.executeUpdate();
        } 
        catch (final SQLException ex) 
        {
            throw new RuntimeException("Failed to log record retrieval audit event", ex);
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
        statement.setString(3, event.getUserSecurityGroups().stream()
            .collect(Collectors.joining(", ")));
        statement.setString(4, event.getUserSecurityPermissions().toString());
        statement.setString(5, event.getClientUserAgent());
        statement.setString(6, event.getClientIPAddress());
    }
        
}
