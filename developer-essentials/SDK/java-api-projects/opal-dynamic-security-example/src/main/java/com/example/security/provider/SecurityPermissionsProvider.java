/*
 * MIT License
 *
 * Copyright (c) 2023, N. Harris Computer Corporation
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

package com.example.security.provider;

import com.i2group.disco.security.spi.IDimension;
import com.i2group.disco.security.spi.IPermission;
import com.i2group.disco.security.spi.IPermission.AccessLevel;
import com.i2group.disco.security.spi.ISecurityPermissionsProvider;
import com.i2group.disco.security.spi.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of {@link ISecurityPermissionsProvider} that provides permissions containing
 * the Security Compartment dimension values for each <i>user</i> in the i2 Analyze deployment.
 */
public final class SecurityPermissionsProvider implements ISecurityPermissionsProvider
{
  private static final String ANALYST = "Analyst";
  private static final String MANAGER = "Manager";
  private static final String DEMO = "Demo";

  private static final String HI = "HI";
  private static final String OSI = "OSI";

  private final Map<String, List<IPermission>> mUserSecurityPermissions = new HashMap<>();
  private final List<IPermission> mDefaultPermissions = new ArrayList<>();

  @Override
  public List<IPermission> getSecurityPermissions(
      final String principalName,
      final Collection<String> userGroups,
      final Collection<IDimension> accessSecurityDimensions)
  {
    // The returned permissions must only contain dimension value identifiers that were present in
    // the accessSecurityDimensions parameter.
    //
    // In this case, we know that the example provider is providing permissions only for values
    // from the Security Compartment dimension, which are also being provided by this sample
    // project. Those values have the identifiers 'HI' and 'OSI', and we can be certain in this
    // case that the permissions we're returning are valid.
    //
    // The userGroups parameter is important when you need to know which groups a user is a member
    // of, in order to determine their permissions. In this example, group membership is not
    // important because we're assigning permissions on a per-user basis.
    return mUserSecurityPermissions.getOrDefault(principalName, mDefaultPermissions);
  }

  @Override
  public void onStartup()
  {
    // The Analyst user receives UPDATE access to HI records and READ_ONLY access to OSI records.
    addPermissions(ANALYST, Map.of(HI, AccessLevel.UPDATE, OSI, AccessLevel.READ_ONLY));

    // The Manager user receives UPDATE access to OSI records (and no access to HI records).
    addPermissions(MANAGER, Map.of(OSI, AccessLevel.UPDATE));

    // The Demo user receives READ_ONLY access to HI records and UPDATE access to OSI records.
    addPermissions(DEMO, Map.of(HI, AccessLevel.READ_ONLY, OSI, AccessLevel.UPDATE));

    // If other users are added to the system, they receive a default set or permissions:
    // READ_ONLY access to OSI records (and no access to HI records).
    mDefaultPermissions.addAll(createPermissions(Map.of(OSI, AccessLevel.READ_ONLY)));
  }

  private void addPermissions(final String principalName, final Map<String, AccessLevel> permissions)
  {
    mUserSecurityPermissions.put(principalName, createPermissions(permissions));
  }

  private static List<IPermission> createPermissions(final Map<String, AccessLevel> permissions)
  {
    return permissions.entrySet().stream()
        .map(entry -> createPermission(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  private static IPermission createPermission(final String dimensionValueId, final AccessLevel accessLevel)
  {
    return Permission.create(dimensionValueId, accessLevel);
  }
}
