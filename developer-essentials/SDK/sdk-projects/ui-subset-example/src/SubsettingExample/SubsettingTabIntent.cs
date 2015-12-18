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
    /// The external contract for <see cref="SubsettingTabIntent"/>.
    /// </summary>
    public interface ISubsettingTabIntent : IIntent
    {
        /// <summary>
        /// The Data object that contains the subset.
        /// </summary>
        object Data { get; }
    }

    /// <summary>
    /// A portal intent for opening a tab to display subsetting information.
    /// </summary>
    public class SubsettingTabIntent : ISubsettingTabIntent
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="SubsettingTabIntent"/> class.
        /// </summary>
        /// <param name="data">The subset data that we are interested in.</param>
        public SubsettingTabIntent(object data)
        {
            Data = data;
        }

        /// <inheritdoc />
        public object Data { get; private set; }
    }
}
