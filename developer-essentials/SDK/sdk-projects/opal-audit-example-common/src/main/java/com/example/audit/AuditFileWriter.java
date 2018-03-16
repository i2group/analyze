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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Class to handle writing entries to an audit log file, providing configurable
 * log rotation.
 * <p>
 * This implementation provides a thin wrapper around {@link FileHandler} to do
 * the writing to file.
 */
final class AuditFileWriter
{
    private final FileHandler mHandler;

    /**
     * Constructor.
     * @param config configuration properties.
     * @throws RuntimeException if the log file cannot be written.
     * @throws IllegalArgumentException if configuration values are invalid.
     */
    public AuditFileWriter(final FileConfiguration config)
    {
        mHandler = createFileHandler(config);
        configureFileHandler();
        addShutdownHook();
    }

    /**
     * Create and configure a FileHandler to use for writing to the audit log.
     * @param config configuration properties.
     * @throws RuntimeException if the log file cannot be opened for writing.
     */
    private FileHandler createFileHandler(final FileConfiguration config)
    {
        try
        {
            // Specify append mode to avoid previous audit logs being overwritten.
            return new FileHandler(config.getFileName(),
                    config.getSizeLimit(),
                    config.getFileCount(),
                    true);
        }
        catch (SecurityException | IOException ex)
        {
            throw new RuntimeException("Failed to open log file: " + config.getFileName(), ex);
        }
    }

    /**
     * Configure the file handler for writing audit log records.
     */
    private void configureFileHandler()
    {
        mHandler.setLevel(Level.ALL);
        mHandler.setFormatter(new Formatter()
        {
            @Override
            public String format(final LogRecord record)
            {
                return record.getMessage() + System.lineSeparator();
            }
        });
    }

    /**
     * Add a shutdown hook to close the FileHandler when the JVM stops. This is
     * necessary to tidy up the lock files created by the logging framework.
     */
    private void addShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                mHandler.close();
            }
        });
    }

    /**
     * Write an audit log entry to the audit log file.
     * @param text description of the audit log event. A null entry is silently ignored.
     */
    public void writeAuditLog(final String text)
    {
        final LogRecord record = new LogRecord(Level.INFO, text);
        mHandler.publish(record);
    }
}
