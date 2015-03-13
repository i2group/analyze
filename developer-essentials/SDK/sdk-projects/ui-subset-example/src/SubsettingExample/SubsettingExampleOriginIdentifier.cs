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
using System;
using System.Linq;
using i2.Apollo.Common.Data;

namespace SubsettingExample
{
    /// <summary>
    /// A factory for assisted injection of <see cref="ISubsettingExampleOriginIdentifier"/>s.
    /// </summary>
    public interface ISubsettingExampleOriginIdentifierFactory
    {
        /// <summary>
        /// Creates a <see cref="ISubsettingExampleOriginIdentifier"/> based on the specified origin identifier.
        /// </summary>
        ISubsettingExampleOriginIdentifier Create(IOriginIdentifier originIdentifier);
    }

    /// <summary>
    /// The external contract for <see cref="SubsettingExampleOriginIdentifier"/>.
    /// </summary>
    public interface ISubsettingExampleOriginIdentifier
    {
        /// <summary>
        /// The identifier of an item in the subsetting example XML file,
        /// as used in the lookup URL.
        /// </summary>
        string Id { get; }

        /// <summary>
        /// The name of the subsetting example XML file, as used in the lookup
        /// URL.
        /// </summary>
        string Source { get; }
    }

    /// <summary>
    /// A wrapper around <see cref="IOriginIdentifier"/> information for
    /// retrieving source data and identifier information for an item.
    /// </summary>
    public class SubsettingExampleOriginIdentifier : ISubsettingExampleOriginIdentifier
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="SubsettingExampleOriginIdentifier"/> class.
        /// </summary>
        public SubsettingExampleOriginIdentifier(IOriginIdentifier originIdentifier, ISubsetExampleOriginHelper subsetExampleOriginHelper)
        {
            if (!subsetExampleOriginHelper.OriginatesFromExampleDataSource(originIdentifier))
            {
                throw new ArgumentException("The specified origin is not from the subsetting example.");
            }

            // If either of these fails, then the structure of the information
            // is different from how it was originally specified in the example.
            Source = originIdentifier.Key.ElementAt(0);
            Id = originIdentifier.Key.ElementAt(1);
        }

        /// <inheritdoc />
        public string Id { get; private set; }

        /// <inheritdoc />
        public string Source { get; private set; }
    }
}
