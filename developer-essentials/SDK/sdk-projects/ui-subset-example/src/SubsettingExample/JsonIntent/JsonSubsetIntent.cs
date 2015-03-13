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
using System.Runtime.Serialization;

namespace SubsettingExample.JsonIntent
{
    /// <summary>
    /// A JSON intent from the HTML page in the subsetting example.
    /// </summary>
    [DataContract]
    public class JsonSubsetIntent
    {
        /// <summary>
        /// Gets or sets the type of the portal intent to launch.
        /// </summary>
        /// <remarks>
        /// In general, a "portal intent" can be anything at all. It is a flag
        /// that helps the Silverlight code to understand what the user who
        /// interacted with the HTML page wanted to do. In this example, the
        /// portal intent is a string that indicates whether the user wanted to
        /// run a visual query or a browse operation.
        /// </remarks>
        [DataMember(Name = "portalIntentToLaunch")]
        public string PortalIntentToLaunch { get; set; }

        /// <summary>
        /// Gets or sets the token for the subset that the platform creates.
        /// </summary>
        [DataMember(Name = "token")]
        public string Token { get; set; }

        /// <summary>
        /// Gets or sets the identifier for the data source from which the
        /// subset was created.
        /// </summary>
        [DataMember(Name = "dataSourceId")]
        public string DataSourceId { get; set; }

        /// <summary>
        /// Gets or sets the user-friendly name of the subset, which is
        /// displayed in the Intelligence Portal when browsing or searching.
        /// </summary>
        [DataMember(Name = "subsetName")]
        public string SubsetName { get; set; }
    }
}
