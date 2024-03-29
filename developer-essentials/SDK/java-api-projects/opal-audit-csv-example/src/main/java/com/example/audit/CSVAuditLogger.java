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

import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;

import com.i2group.disco.audit.IAuditEvent;
import com.i2group.disco.audit.IExpandAuditEvent;
import com.i2group.disco.audit.IQuickSearchAuditEvent;
import com.i2group.disco.audit.IRecordRetrievalAuditEvent;
import com.i2group.disco.audit.IRecordUploadAuditEvent;
import com.i2group.disco.audit.IVisualQueryAuditEvent;
import com.i2group.disco.audit.spi.IAuditLogger;

/**
 * Audit logger implementation that writes a record to a CSV file for each audit
 * event received. These CSV records can later be viewed in a tool such as
 * Excel, or loaded into a database.
 * <p>
 * The fields within each CSV record are:
 * <ol>
 * <li>Timestamp</li>
 * <li>User ID</li>
 * <li>Audit operation</li>
 * <li>User security groups</li>
 * <li>Client IP address</li>
 * <li>Client user agent</li>
 * <li>Detail specific to the audit operation</li>
 * </ol>
 */
public final class CSVAuditLogger
    implements IAuditLogger
{
    /** Description of Quick Search audit operations. */
    private static final String TYPE_QUICK_SEARCH = "QuickSearch";
    /** Description of Expand audit operations. */
    private static final String TYPE_EXPAND = "Expand";
    /** Description of Visual Query audit operations. */
    private static final String TYPE_VISUAL_QUERY = "VisualQuery";
    /** Description of Record Retrieval audit operations. */
    private static final String TYPE_RECORD_RETRIEVAL = "RecordRetrieval";
    /** Description of Record Upload audit operations. */
    private static final String TYPE_RECORD_UPLOAD = "RecordUpload";

    /** Utility for formatting CSV file entries, including escaping and quoting. */
    private static final CSVFormat CSV_FORMAT = CSVFormat.RFC4180;

    private final AuditFileWriter mLogWriter = new AuditFileWriter(new FileConfiguration("CSVAudit.properties"));

    @Override
    public boolean isQueryAuditEnabled()
    {
        return true;
    }

    @Override
    public void logQuickSearch(final IQuickSearchAuditEvent event)
    {
        final String detail =
            "DataStores: " + event.getDataStores()
            + ", Expression: " + event.getExpression()
            + ", Filters: " + event.getFilters();
        writeAuditLog(event, TYPE_QUICK_SEARCH, detail);
    }

    @Override
    public void logExpand(final IExpandAuditEvent event)
    {
        final String detail = "Seeds: " + event.getSeedRecords();
        writeAuditLog(event, TYPE_EXPAND, detail);
    }

    @Override
    public void logVisualQuery(final IVisualQueryAuditEvent event)
    {
        final String detail =
            "DataStores: " + event.getDataStores()
            + ", Query: " + event.getQuery();
        writeAuditLog(event, TYPE_VISUAL_QUERY, detail);
    }

    @Override
    public boolean isRecordRetrievalAuditEnabled()
    {
        return true;
    }

    @Override
    public void logRecordRetrieval(final IRecordRetrievalAuditEvent event)
    {
        final String detail = "Records: " + event.getRecords();
        writeAuditLog(event, TYPE_RECORD_RETRIEVAL, detail);
    }

    @Override
    public boolean isRecordCUDAuditEnabled()
    {
        return true;
    }

    @Override
    public void logRecordUpload(final IRecordUploadAuditEvent event)
    {
        final String detail = "Created Records: " + event.getCreatedRecords()
                + ", Modified Records: " + event.getModifiedRecords();
        writeAuditLog(event, TYPE_RECORD_UPLOAD, detail);
    }

    /**
     * Write a CSV record in the audit log for the supplied audit event,
     * including additional information specific to the sub-type of the audit
     * event.
     *
     * @param event an audit event.
     * @param type specific type of the audit event.
     * @param detail information specific to this audit event type.
     */
    private void writeAuditLog(final IAuditEvent event, final String type, final String detail)
    {
        final String logEntry = CSV_FORMAT.format(
            event.getTimestamp(),
            event.getUser(),
            type,
            event.getUserSecurityGroups().stream()
                .collect(Collectors.joining(", ")),
            event.getClientIPAddress(),
            event.getClientUserAgent(),
            detail);
        mLogWriter.writeAuditLog(logEntry);
    }

}
