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
using System.Globalization;
using i2.Apollo.Common.DependencyInjection;
using i2.Apollo.Common.Preferences;

namespace SubsettingExample
{
    /// <summary>
    /// The external contract for <see cref="SubsettingExampleLocations"/>.
    /// </summary>
    [ImplementedBy(typeof(SubsettingExampleLocations), singleton:true)]
    public interface ISubsettingExampleLocations
    {
        /// <summary>
        /// Gets the location of the external subset generation UI.
        /// </summary>
        Uri ExternalSubsetGenerationUri { get; }

        /// <summary>
        /// Generates the location of an HTML page to display an item.
        /// </summary>
        Uri GeneratePathToItem(string id, string source);
    }

    public class SubsettingExampleLocations : ISubsettingExampleLocations
    {
        // To override these preferences without recompiling the example,
        // you can add elements named SubsettingExampleDisplayRecordUrl and
        // SubsettingExampleGenerationUILocation to ApolloClientSettings.xml.
        private static readonly ApplicationPreference<string> SubsettingExampleDisplayRecordUrlPreference =
            ApplicationPreference.Register("SubsettingExampleDisplayRecordUrl",
                                           ApplicationPreference.DefaultStringParser,
                                           "http://localhost:20032/ItemDetails.jsp?id={0}&amp;source={1}");

        private static readonly ApplicationPreference<string> SubsettingExampleGenerationUILocationPreference =
            ApplicationPreference.Register("SubsettingExampleGenerationUILocation",
                                           ApplicationPreference.DefaultStringParser,
                                           "http://localhost:20032/SubsettingHtml.html");

        private readonly string mSubsettingExampleUriFormat;

        /// <summary>
        /// Initializes a new instance of the <see cref="SubsettingExampleLocations"/> class.
        /// </summary>
        public SubsettingExampleLocations(IApplicationPreferences applicationPreferences)
        {
            var generationUi = applicationPreferences.GetValue(SubsettingExampleGenerationUILocationPreference);
            ExternalSubsetGenerationUri = new Uri(generationUi, UriKind.RelativeOrAbsolute);

            mSubsettingExampleUriFormat = applicationPreferences.GetValue(SubsettingExampleDisplayRecordUrlPreference);
        }

        /// <inheritdoc />
        public Uri GeneratePathToItem(string id, string source)
        {
            var location = string.Format(CultureInfo.InvariantCulture,
                                         mSubsettingExampleUriFormat,
                                         id,
                                         source);

            return new Uri(location, UriKind.RelativeOrAbsolute);
        }

        /// <inheritdoc />
        public Uri ExternalSubsetGenerationUri { get; private set; }
    }
}
