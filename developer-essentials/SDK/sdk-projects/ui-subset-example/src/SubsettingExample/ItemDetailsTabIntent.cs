/*******************************************************************************
 * Copyright (c) 2015 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and/or initial documentation
 *******************************************************************************/
using i2.Apollo.Common.Intent;

namespace SubsettingExample
{
    /// <summary>
    /// The external contract for <see cref="ItemDetailsTabIntent"/>.
    /// </summary>
    public interface IItemDetailsTabIntent : IIntent
    {
        /// <summary>
        /// The identifier of the item in the subsetting example XML file,
        /// which is used in the lookup URL.
        /// </summary>
        string Id { get; }

        /// <summary>
        /// The name of the subsetting example XML file, which is used in the
        /// lookup URL.
        /// </summary>
        string Source { get; }
    }

    /// <summary>
    /// A portal intent for opening a tab to display details about an item that
    /// originates in the data in an XML file.
    /// </summary>
    public class ItemDetailsTabIntent : IItemDetailsTabIntent
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ItemDetailsTabIntent"/> class.
        /// </summary>
        public ItemDetailsTabIntent(string id, string source)
        {
            Id = id;
            Source = source;
        }

        /// <inheritdoc />
        public string Id { get; private set; }

        /// <inheritdoc />
        public string Source { get; private set; }
    }
}
