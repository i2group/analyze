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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import com.example.jaxb.Actor;
import com.example.jaxb.Association;
import com.example.jaxb.Data;
import com.example.jaxb.DataItem;
import com.example.jaxb.DataMarshaller;
import com.i2group.utils.exception.WrappedCheckedException;

/**
 * A set of methods for retrieving property names and values from a
 * {@link DataItem} that originated in an external data source.
 */
public final class ExternalDataRetrieval
{
    private static final int ACCESSOR_PREFIX_OFFSET = 3;
    private final Map<String, String> mItemProperties;
    private final DataMarshaller mDataMarshaller;

    /**
     * Constructs a new {@link ExternalDataRetrieval}.
     * 
     * @param id
     *            A {@link String} that contains the identifier of the
     *            {@link DataItem} from which property names and values are to
     *            be retrieved.
     * @param source
     *            A {@link String} that contains the name of the file that
     *            represents the external data source in this example.
     */
    public ExternalDataRetrieval(final String id, final String source)
    {
        mDataMarshaller = new DataMarshaller();
        mItemProperties = retrieveItemPropertiesFromSourceData(id, source);
    }

    /**
     * Gets a map that contains the property names and values from the
     * {@link DataItem}.
     * 
     * @return See above.
     */
    public Map<String, String> getItemProperties()
    {
        return mItemProperties;
    }

    /**
     * Populates a map with property names and values from a {@link DataItem}.
     * 
     * @param itemId
     *            A {@link String} that contains the identifier of the
     *            {@link DataItem} from which property names and values are to
     *            be retrieved.
     * @param source
     *            A {@link String} that contains the name of the file that
     *            represents the external data source in this example.
     * @return See above.
     */
    private Map<String, String> retrieveItemPropertiesFromSourceData(
            final String itemId, final String source)
    {
        final Map<String, String> itemProperties = new LinkedHashMap<String, String>();

        final Data dataFromSource = mDataMarshaller
                .unmarshallFromSourceFile(source);

        for (DataItem dataItem : dataFromSource.getDataItems())
        {
            if (dataItem.getId().equals(itemId))
            {
                if (dataItem instanceof Actor)
                {
                    populateProperties(itemProperties, (Actor) dataItem);
                }
                if (dataItem instanceof Association)
                {
                    populateProperties(itemProperties, (Association) dataItem);
                }
            }
        }
        return itemProperties;
    }

    /**
     * Populates the specified map with property names and values extracted
     * from the specified {@link DataItem}.
     * 
     * @param <T>
     *            The type of the {@link DataItem} from which property names
     *            and values are to be extracted.
     * @param itemProperties
     *            The map that is to be populated.
     * @param dataItem
     *            The {@link DataItem} from which property names and values
     *            are to be extracted.
     */
    private <T extends DataItem> void populateProperties(
            final Map<String, String> itemProperties, final T dataItem)
    {
        final Method[] methods = dataItem.getClass().getMethods();
        for (Method method : methods)
        {
            final String methodName = method.getName();
            if (methodName.startsWith("get") && !methodName.equals("getClass") && !methodName.equals("getId"))
            {
                Object value;
                try
                {
                    value = method.invoke(dataItem, new Object[] {});
                }
                catch (IllegalArgumentException ex)
                {
                    throw ex;
                }
                catch (IllegalAccessException ex)
                {
                    throw new WrappedCheckedException(ex);
                }
                catch (InvocationTargetException ex)
                {
                    throw new WrappedCheckedException(ex);
                }
                itemProperties.put(
                        methodName.substring(ACCESSOR_PREFIX_OFFSET),
                        value.toString());
            }
        }
    }
}
