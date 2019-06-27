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
package com.example;

import java.text.MessageFormat;

/**
 * A container for the entry point to the "data load direct" example project.
 */
public final class ExampleDataLoaderMain
{
    private static final String LOAD_ARG = "-load";
    private static final String SYNCHRONIZE_ARG = "-sync";
    private static final String PURGE_ARG = "-purge";

    /**
     * Constructs a new {@link ExampleDataLoaderMain}.
     */
    private ExampleDataLoaderMain()
    {
    }

    /**
     * Main entry point.
     * 
     * @param args
     *            {@link String}s containing the command-line arguments for the
     *            example loader.
     */
    public static void main(final String[] args)
    {
        final String help = MessageFormat.format(
                "Please supply one of: {0}, {1}, {2}", LOAD_ARG,
                SYNCHRONIZE_ARG, PURGE_ARG);

        if (args == null || args.length != 1)
        {
            System.out.println(help);
            return;
        }

        final String option = args[0].toLowerCase();

        // This forces the AR data loader to read the configuration settings
        // from the loader.properties file
        // (rather than the default ApolloServerSettings.properties file).
        System.setProperty("ApolloServerSettingsResource", "loader.properties");

        final ExampleDataLoader exampleDataLoader = new ExampleDataLoader();

        if (LOAD_ARG.equals(option))
        {
            System.out.println("Invoking 'load'...");
            exampleDataLoader.load();
            System.out.println("...'load' completed.");
            return;
        }

        if (SYNCHRONIZE_ARG.equals(option))
        {
            System.out.println("Invoking 'synchronize'...");
            exampleDataLoader.synchronize();
            System.out.println("...'synchronize' completed.");
            return;
        }

        if (PURGE_ARG.equals(option))
        {
            System.out.println("Invoking 'purge'...");
            exampleDataLoader.purge();
            System.out.println("...'purge' completed.");
            return;
        }

        System.out.println(help);
    }
}
