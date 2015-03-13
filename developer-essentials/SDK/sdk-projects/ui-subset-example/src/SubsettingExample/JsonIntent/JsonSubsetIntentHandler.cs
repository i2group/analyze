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
using System.Linq;
using System.Windows.Browser;
using i2.Apollo.Common;
using i2.Apollo.Common.Data;
using i2.Apollo.Common.DependencyInjection;
using i2.Apollo.Common.Intent;
using i2.Apollo.Common.Models.Explore;
using i2.Apollo.Common.Models.NetworkSearch;
using i2.Apollo.CommonControls;
using i2.Apollo.Controls.Intent;
using i2.Apollo.Controls.TabFramework;

namespace SubsettingExample.JsonIntent
{
    /// <summary>
    /// A handler for JSON subset intents.
    /// </summary>
    [ImplementedBy(typeof(JsonSubsetSubsetIntentHandler), singleton:true)]
    public interface IJsonSubsetIntentHandler
    {
        /// <summary>
        /// Gets the name of the JSON intent handler, which is registered through <see cref="IHtmlPage.RegisterScriptableObject"/>.
        /// </summary>
        string ScriptKey { get; }
    }

    /// <summary>
    /// An <see cref="IJsonSubsetIntentHandler"/> for the JSON intents that are
    /// sent by the HTML page in the subsetting example.
    /// </summary>
    public class JsonSubsetSubsetIntentHandler : IJsonSubsetIntentHandler
    {
        private const string VisualQueryIntentType = "VisualQuery";
        private const string BrowseIntentType = "Browse";
        private readonly IIntentManager mIntentManager;
        private readonly IJsonSerializerWrapper mJsonSerializerWrapper;
        private readonly INetworkSearchConfigBuilder mNetworkSearchConfigBuilder;
        private readonly IDataSourcesAndSchema mDataSourcesAndSchema;
        private readonly IExploreConfigBuilder mExploreConfigBuilder;
        private readonly IExplorationIntentFactory mExplorationIntentFactory;
        private readonly IJavaScriptHostedCallRunner mJavaScriptHostedCallRunner;

        /// <summary>
        /// Initializes a new instance of the <see cref="JsonSubsetSubsetIntentHandler"/> class.
        /// </summary>
        public JsonSubsetSubsetIntentHandler(IIntentManager intentManager, 
            IJsonSerializerWrapper jsonSerializerWrapper,
            INetworkSearchConfigBuilder networkSearchConfigBuilder,
            IDataSourcesAndSchema dataSourcesAndSchema,
            IExploreConfigBuilder exploreConfigBuilder,
            IExplorationIntentFactory explorationIntentFactory,
            IJavaScriptHostedCallRunner javaScriptHostedCallRunner)
        {
            mIntentManager = intentManager;
            mJsonSerializerWrapper = jsonSerializerWrapper;
            mNetworkSearchConfigBuilder = networkSearchConfigBuilder;
            mDataSourcesAndSchema = dataSourcesAndSchema;
            mExploreConfigBuilder = exploreConfigBuilder;
            mExplorationIntentFactory = explorationIntentFactory;
            mJavaScriptHostedCallRunner = javaScriptHostedCallRunner;
        }

        /// <summary>
        /// Handles Windows events from JavaScript code. See the method named
        /// InjectPostMessageToIntentHandlerToHostPage in Module.cs.
        /// </summary>
        [ScriptableMember(ScriptAlias = "handleIntent")]
        public void HandleIntent(string data)
        {
            // Ensure any errors in the execution are handled in Silverlight.
            mJavaScriptHostedCallRunner.RunJavaScriptHostedCall(() => ProcessJsonIntent(data));
        }

        /// <inheritdoc />
        public string ScriptKey
        {
            get { return "SubsettingExampleJsonIntentHandler"; }
        }

        private void ProcessJsonIntent(string data)
        {
            // We are expecting to receive strings that represent serialized
            // JsonSubsetIntent classes.
            var jsonIntent = mJsonSerializerWrapper.Deserialize<JsonSubsetIntent>(data);

            // Determine which data source the intent refers to.
            var dataSource = mDataSourcesAndSchema.DataSources.SingleOrDefault(x => x.Id.Equals(jsonIntent.DataSourceId));

            if (dataSource == null)
            {
                // No data source matched the intent, so throw an exception.
                throw new ArgumentException(string.Format(CultureInfo.InvariantCulture,
                                                          "Unable to find data source with ID '{0}'",
                                                          jsonIntent.DataSourceId));
            }

            // Generate a subset identifier from the token and the name in the
            // intent. The identifier ensures that any Browse or Visual Query
            // operation runs over the subset that the token defines. The name
            // is displayed to the user to identify which subset they are
            // working with.
            var externalDataSubsetIdentifier = new ExternalDataSubsetIdentifier(jsonIntent.Token, jsonIntent.SubsetName);

            IIntent intent;

            // Generate an appropriate intent, according to the analytic
            // operation that the user selected in SubsettingHtml.html.
            switch (jsonIntent.PortalIntentToLaunch)
            {
                case VisualQueryIntentType:
                    var networkSearchConfig = mNetworkSearchConfigBuilder
                        .StartNew()
                        .SetDataSource(dataSource)
                        .SetExternalDataSubsetIdentifier(externalDataSubsetIdentifier)
                        .Build();

                    intent = new OpenNetworkSearchWithConfig(networkSearchConfig, false);
                    break;

                case BrowseIntentType:
                    var exploreConfig = mExploreConfigBuilder
                        .StartNew()
                        .SetDataSource(dataSource)
                        .SetExternalDataSubsetIdentifier(externalDataSubsetIdentifier)
                        .Build();

                    intent = mExplorationIntentFactory.CreateExploreIntent(exploreConfig);
                    break;
                default:
                    throw new ArgumentException("Unknown Portal intent: " + jsonIntent.PortalIntentToLaunch);
            }

            // Open the tab for the results, next to the current tab.
            var openTabOptions = new OpenTabOptions
                                     {
                                         Location = OpenTabLocation.OpenNearCurrent
                                     };

            // Fire off the intent.
            mIntentManager.Run(intent, openTabOptions);
        }
    }
}
