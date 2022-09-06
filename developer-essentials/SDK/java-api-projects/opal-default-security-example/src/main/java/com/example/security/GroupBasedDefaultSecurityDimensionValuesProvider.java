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

package com.example.security;

import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

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
            final Principal principal, final Set<String> userGroupsSet, final SecuritySchema securitySchema)
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

        Collection<com.i2group.disco.user.spi.DefaultSecurityDimension> defaultSecurityDimensions =
                createOrderedDefaultSecurityDimension(securitySchema, dimensionValueIdsByDimensionId);
        return defaultSecurityDimensions;
    }

    private void addDefaultValueIds(final Multimap<String, String> dimensionValueIdsByDimensionId,
        final GroupPermissions groupPermissions)
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
     * @param securitySchema The security schema
     * @param dimensionValueIdsByDimensionId The dimension value id by dimension id.
     * @return Collection<DefaultSecurityDimension> The collection.
     */
    private Collection<com.i2group.disco.user.spi.DefaultSecurityDimension> createOrderedDefaultSecurityDimension(
            final SecuritySchema securitySchema, final Multimap<String, String> dimensionValueIdsByDimensionId)
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
                    if (isOrdered && !orderedValueIds.isEmpty())
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
