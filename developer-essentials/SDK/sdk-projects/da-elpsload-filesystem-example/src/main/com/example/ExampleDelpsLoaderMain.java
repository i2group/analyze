/*
 * Copyright (c) 2014 IBM Corp.
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
 * A container for the entry point to the "data load ELP stage" example project.
 */
public final class ExampleDelpsLoaderMain
{
    private static final String LOAD_ARG = "-load";
    private static final String SYNCHRONIZE_ARG = "-sync";
    private static final String PURGE_ARG = "-purge";

    /**
     * Constructs a new {@link ExampleDelpsLoaderMain}.
     */
    private ExampleDelpsLoaderMain()
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

        // This forces the ELP Stage Repository (ER) data loader to read the
        // configuration settings from the loader.properties file (rather than
        // the default ApolloServerSettings.properties file).
        System.setProperty("ApolloServerSettingsResource", "loader.properties");

        final ExampleDelpsLoader exampleDelpsLoader = new ExampleDelpsLoader();

        if (LOAD_ARG.equals(option))
        {
            System.out.println("Invoking 'load'...");
            exampleDelpsLoader.load();
            System.out.println("...'load' completed.");
            return;
        }

        if (SYNCHRONIZE_ARG.equals(option))
        {
            System.out.println("Invoking 'synchronize'...");
            exampleDelpsLoader.synchronize();
            System.out.println("...'synchronize' completed.");
            return;
        }

        if (PURGE_ARG.equals(option))
        {
            System.out.println("Invoking 'purge'...");
            exampleDelpsLoader.purge();
            System.out.println("...'purge' completed.");
            return;
        }

        System.out.println(help);
    }
}
