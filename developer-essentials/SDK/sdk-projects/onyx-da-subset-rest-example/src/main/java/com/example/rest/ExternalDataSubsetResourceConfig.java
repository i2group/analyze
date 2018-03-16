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
package com.example.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * The REST application for this example project.
 */
public final class ExternalDataSubsetResourceConfig extends Application
{
    @Override
    public Set<Class<?>> getClasses()
    {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(ExternalDataSubsetResource.class);
        return classes;
    }
}
