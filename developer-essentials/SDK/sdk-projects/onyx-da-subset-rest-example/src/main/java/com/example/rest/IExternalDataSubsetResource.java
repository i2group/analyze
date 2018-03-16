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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A REST resource for subset creation.
 */
public interface IExternalDataSubsetResource
{
    /**
     * Creates a subset in response to the specified {@link SubsetRequest}.
     * 
     * @param subsetRequest
     *            The {@link SubsetRequest} to be acted upon.
     * @return A {@link Response} that contains the created subset.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response createSubset(SubsetRequest subsetRequest);
}
