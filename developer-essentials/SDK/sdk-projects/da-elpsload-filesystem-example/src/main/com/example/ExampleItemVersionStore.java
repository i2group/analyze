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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * A persistent store for the item keys and version numbers that are
 * returned by the methods of
 * {@link com.i2group.apollo.externaldata.loader.IDataLoader}.
 */
public final class ExampleItemVersionStore
{
    private static final String VERSIONS_FILE = "ItemKeyToVersion.properties";

    private final Properties mItemKeyToVersion;

    private boolean mKeysStoreIsDirty = false;

    /**
     * Constructs a new {@link ExampleItemVersionStore}.
     */
    public ExampleItemVersionStore()
    {
        mItemKeyToVersion = new Properties();
    }

    /**
     * Loads the item identifiers and version numbers from persistent
     * storage into memory.
     */
    public void load()
    {
        loadKeys();
        mKeysStoreIsDirty = false;
    }

    /**
     * Saves the item identifiers and version numbers from memory to persistent
     * storage.
     */
    public void store()
    {
        if (mKeysStoreIsDirty)
        {
            storeKeys();
            mKeysStoreIsDirty = false;
        }
    }

    /**
     * Clears the in-memory and persistent copies of the store of item
     * identifiers and version numbers.
     */
    public void clear()
    {
        mItemKeyToVersion.clear();
        storeKeys();
        mKeysStoreIsDirty = false;
    }

    /**
     * Updates the item version numbers in the store to those provided in the
     * specified map.
     * 
     * @param itemVersionMap
     *            A map of {@link String} containing item keys to their new
     *            version numbers.
     */
    public void updateItemVersions(final Map<String, Long> itemVersionMap)
    {
        for (Entry<String, Long> entry : itemVersionMap.entrySet())
        {
            final String itemKey = entry.getKey();

            final Long version = entry.getValue();

            updateItemVersion(itemKey, version);
        }
    }

    /**
     * Updates the version number related to the specified item key in the store
     * to the specified value.
     * 
     * @param itemKey
     *            A {@link String} that contains the key of an item in the
     *            store.
     * @param version
     *            The new version number of the item (in the ELP stage).
     */
    public void updateItemVersion(final String itemKey, final Long version)
    {
        mItemKeyToVersion.setProperty(itemKey, version.toString());
        mKeysStoreIsDirty = true;
    }

    /**
     * Gets the version number of the item with the specified key in the store.
     * 
     * @param itemKey
     *            A {@link String} that contains the key of an item in the
     *            store.
     * @return The version number of the specified item.
     */
    public Long getItemVersion(final String itemKey)
    {
        final String version = mItemKeyToVersion.getProperty(itemKey);
        Long itemVersion = null;

        if (version != null)
        {
            itemVersion = Long.valueOf(version);
        }

        return itemVersion;
    }

    /**
     * Loads the item identifiers and version numbers from persistent
     * storage into memory.
     */
    private void loadKeys()
    {
        try
        {
            mItemKeyToVersion.load(new FileInputStream(VERSIONS_FILE));
        }
        catch (IOException ex)
        {
            // The file doesn't exist - it will be created in the storeKeys()
            // method.
            return;
        }
    }

    /**
     * Saves the item identifiers and version numbers from memory to persistent
     * storage.
     */
    private void storeKeys()
    {
        try
        {
            mItemKeyToVersion.store(new FileOutputStream(VERSIONS_FILE),
                    "ELP stage item key to item version number mappings.");
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
