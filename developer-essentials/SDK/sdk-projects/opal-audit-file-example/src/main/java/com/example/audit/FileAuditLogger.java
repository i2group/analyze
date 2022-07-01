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

import com.i2group.disco.audit.IAuditEvent;
import com.i2group.disco.audit.spi.IAuditLogger;

/**
 * Audit logger implementation that writes entries to an audit log file using
 * the default string representation for audit events.
 */
public final class FileAuditLogger
    implements IAuditLogger
{
    private final AuditFileWriter mLogWriter = new AuditFileWriter(new FileConfiguration("FileAudit.properties"));

    @Override
    public boolean isQueryAuditEnabled()
    {
        return true;
    }

    @Override
    public boolean isRecordRetrievalAuditEnabled()
    {
        return true;
    }

    @Override
    public boolean isRecordCUDAuditEnabled()
    {
        return true;
    }

    @Override
    public void logDefault(final IAuditEvent event)
    {
        mLogWriter.writeAuditLog(event.toString());
    }
}
