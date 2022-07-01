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
public final class AuditFileWriter
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
     * @return FileHandler the file handler.
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
