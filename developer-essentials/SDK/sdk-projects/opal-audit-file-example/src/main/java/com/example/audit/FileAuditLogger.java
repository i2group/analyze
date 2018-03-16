/*
 * Copyright (c) 2014, 2018 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
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
