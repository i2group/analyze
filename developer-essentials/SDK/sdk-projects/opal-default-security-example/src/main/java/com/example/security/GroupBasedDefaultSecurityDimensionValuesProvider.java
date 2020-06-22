/*
 * Copyright (c) 2014, 2020 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example.security;

import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.example.security.jaxb.Permissions;
import com.example.security.jaxb.GroupPermissions;
import com.example.security.jaxb.GroupBasedDefaultSecurityDimensions;
import com.example.security.jaxb.Permission;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.i2group.apollo.model.security.dynamic.transport.Dimension;
import com.i2group.apollo.model.security.dynamic.transport.DimensionValue;
import com.i2group.apollo.model.security.dynamic.transport.SecuritySchema;
import com.i2group.disco.user.spi.IDefaultSecurityDimensionValuesProvider;
import com.i2group.utils.ResourceHelper;

/**
 * An implementation of {@link IDefaultSecurityDimensionValuesProvider} that
 * generates {@link com.i2group.disco.user.spi.DefaultSecurityDimension}s based
 * on the current user group membership and the configuration supplied in an XML
 * file.
 */
public final class GroupBasedDefaultSecurityDimensionValuesProvider implements IDefaultSecurityDimensionValuesProvider 
{
    private static final String CONFIG_XML = "group-based-default-security-dimension-values.xml";

    private final JAXBContext mJAXBContext;
    private final GroupBasedDefaultSecurityDimensions mGroupBasedDefaultSecurityDimensions;

    /**
     * Constructs a new {@link GroupBasedDefaultSecurityDimensionValuesProvider}.
     */
    public GroupBasedDefaultSecurityDimensionValuesProvider() 
    {
        // Extracts the group-based-default-security-dimension-values from the xml file into java instances.
        try
        {
            /*
             * JAXBContext is only constructed once as the implementation of the 
             * IDefaultSecurityDimensionValuesProvider is only constructed once.
             */
            mJAXBContext = JAXBContext.newInstance(GroupBasedDefaultSecurityDimensions.class);
            mGroupBasedDefaultSecurityDimensions = unmarshalFromFile();
        }
        catch (JAXBException ex)
        {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Collection<com.i2group.disco.user.spi.DefaultSecurityDimension> getDefaultSecurityDimensions(
            Principal principal, Set<String> userGroupsSet, SecuritySchema securitySchema) 
    {
        // Multimap which where key = dimensionId, value = set of valueIds.
        final Multimap<String, String> dimensionValueIdsByDimensionId = HashMultimap.create();

        // userGroups parsed in will contain the user groups that the user is associated with according to the schema.
        // Use the user groups to map which the dimensionValueIdsByDimensionId
        final Collection<GroupPermissions> groupPermissionsCollection = 
                mGroupBasedDefaultSecurityDimensions.getGroupPermissions();

        for (GroupPermissions groupPermissions : groupPermissionsCollection)
        {
            // Add default dimension value ids for all users.
            if (groupPermissions.isAllUsers())
            {
                addDefaultValueIds(dimensionValueIdsByDimensionId, groupPermissions);
            }
            else
            {
                // Add default dimension value ids for current user.
                if (userGroupsSet.contains(groupPermissions.getUserGroup()))
                {
                    addDefaultValueIds(dimensionValueIdsByDimensionId, groupPermissions);
                }
            }
        }

        final Collection<com.i2group.disco.user.spi.DefaultSecurityDimension> defaultSecurityDimensions = 
                createOrderedDefaultSecurityDimension(securitySchema, dimensionValueIdsByDimensionId);
        return defaultSecurityDimensions;
    }

    private void addDefaultValueIds(Multimap<String, String> dimensionValueIdsByDimensionId,
        GroupPermissions groupPermissions)
    {
        for (Permissions permissions : groupPermissions.getAllGroupPermissions())
        {
            for (Permission permission : permissions.getPermissions())
            {
                dimensionValueIdsByDimensionId.put(permissions.getDimension(), permission.getDimensionValue());
            }
        }
    }

    /**
     * Order {@link com.i2group.disco.user.spi.DefaultSecurityDimension}s and default dimension value ids in schema order.
     */
    private Collection<com.i2group.disco.user.spi.DefaultSecurityDimension> createOrderedDefaultSecurityDimension(
            SecuritySchema securitySchema, Multimap<String, String> dimensionValueIdsByDimensionId)
    {
        final Collection<Dimension> schemaDimensions = securitySchema.getSecurityDimensions().getAccessSecurityPermissions();
        final Collection<com.i2group.disco.user.spi.DefaultSecurityDimension> result = new ArrayList<>();
        for (Dimension schemaDimension : schemaDimensions)
        {
            final String dimensionId = schemaDimension.getId();
            if (dimensionValueIdsByDimensionId.containsKey(dimensionId))
            {
                final Collection<String> valueIds = dimensionValueIdsByDimensionId.get(dimensionId);

                final List<DimensionValue> schemaDimensionValues = schemaDimension.getValues();
                final boolean isOrdered = schemaDimension.isOrdered();
                final Collection<String> orderedValueIds = new ArrayList<>();
                for (DimensionValue schemaDimensionValue : schemaDimensionValues)
                {
                    final String schemaDimensionValueId = schemaDimensionValue.getId();
                    if (valueIds.contains(schemaDimensionValueId))
                    {
                        orderedValueIds.add(schemaDimensionValueId);
                    }
                    // Only include 1 ordered dimension value id.
                    if (isOrdered == true && !orderedValueIds.isEmpty())
                    {
                        break;
                    }
                }

                final com.i2group.disco.user.spi.DefaultSecurityDimension defaultSecurityDimension = 
                        new com.i2group.disco.user.spi.DefaultSecurityDimension(dimensionId, orderedValueIds);
                result.add(defaultSecurityDimension);
            }
        }
        return result;
    }

    private GroupBasedDefaultSecurityDimensions unmarshalFromFile() throws JAXBException
    {
        final Unmarshaller unmarshaller = mJAXBContext.createUnmarshaller();
        final InputStream stream = ResourceHelper.getResourceAsStream(CONFIG_XML);
        final GroupBasedDefaultSecurityDimensions groupBasedDefaultSecurityDimensions = (GroupBasedDefaultSecurityDimensions) unmarshaller.unmarshal(stream);
        return groupBasedDefaultSecurityDimensions;
    }
}
