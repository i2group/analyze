/*******************************************************************************
 * Copyright (c) 2014 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and/or initial documentation
 *******************************************************************************/
using System;
using System.Collections.Generic;
using System.Linq;
using i2.Apollo.Common;
using i2.Apollo.Common.Data;
using i2.Apollo.CommonControls;
using i2.Apollo.Controls.Intent;

namespace SubsettingExample
{
    /// <summary>
    /// An example class that illustrates how to modify the existing command
    /// structure in the Intelligence Portal. Methods in this class add a
    /// command group that contains a command that displays the details of an
    /// item that comes from the example data source.
    /// </summary>
    public class ItemDetailsCommandProvider : IGroupableContextMenuCommandProvider
    {
        // Every entry in the context menu or actions toolbar
        // needs a unique identifier.
        private static readonly Guid ItemDetailsGroupingId = Guid.Parse("{48F9C101-320F-4CE7-8A02-7FFB2587EC07}");
        private static readonly Guid DisplayItemDetailsCommandId = Guid.Parse("{54472AEE-322D-44BC-B87B-CA7FC24A0083}");

        private readonly ICommandsContext mCommandsContext;
        private readonly ICommandContextState mCommandContextState;
        private readonly IIntentManager mIntentManager;
        private readonly ISubsettingExampleOriginIdentifierFactory mSubsettingExampleOriginIdentifierFactory;
        private readonly ISubsetExampleOriginHelper mSubsetExampleOriginHelper;

        /// <summary>
        /// Initializes a new instance of the <see cref="ItemDetailsCommandProvider"/> class.
        /// </summary>
        /// <remarks>
        /// The constructor requires an implementation of ICommandContextState
        /// because ItemDetailsCommandProvider is used with
        /// IGenericContextMenuCommandProviderFactoryRegistry as a factory for
        /// creating commands based on ICommandsContext objects.
        /// </remarks>
        public ItemDetailsCommandProvider(
            ICommandsContext commandsContext, 
            ICommandContextState commandContextState, 
            IIntentManager intentManager,
            ISubsettingExampleOriginIdentifierFactory subsettingExampleOriginIdentifierFactory,
            ISubsetExampleOriginHelper subsetExampleOriginHelper)
        {
            mCommandsContext = commandsContext;
            mCommandContextState = commandContextState;
            mIntentManager = intentManager;
            mSubsettingExampleOriginIdentifierFactory = subsettingExampleOriginIdentifierFactory;
            mSubsetExampleOriginHelper = subsetExampleOriginHelper;
        }

        /// <summary>
        /// Registers a command group to be added to the context menu in the
        /// Intelligence Portal.
        /// This method is called by the Intelligence Portal application.
        /// </summary>
        /// <param name="registrar">The command registrar, which handles the
        /// registration of commands and command groups.</param>
        public void RegisterCommandGroupings(ICommandRegistrar registrar)
        {
            // Add our own group of commands after the refresh, item, and set command groups.
            registrar.RegisterCommandGrouping(ItemDetailsGroupingId, SubsettingExampleStringResources.ItemDetailsCommandGroupHeading, null, Enumerable.Empty<Guid>(), new[] { RefreshCommandProvider.RefreshCommandGroupingType, DefaultItemCommands.ItemCommandsCommandGroupingType, DefaultItemCommands.SetCommandsCommandGroupingType });
        }

        /// <summary>
        /// Registers some additional commands to be added to the actions
        /// toolbar and the context menu in the Intelligence Portal.
        /// This method is called by the Intelligence Portal application.
        /// </summary>
        /// <param name="registrar">The command registrar, which handles the
        /// registration of commands and command groups.</param>
        public void RegisterCommands(ICommandRegistrar registrar)
        {
            // "displayItemDetailsCommand" displays item details, but only if
            // there is exactly one selected item, and that item came from the
            // example data source.
            var displayItemDetailsCommand = new SimpleCommand
                              {
                                  IsEnabled = CanDisplayItemDetails(),
                                  Action = RunDisplayItemDetailsTabIntent
                              };

            var displayItemDetailsNamedCommand = new NamedCommand(SubsettingExampleStringResources.DisplayItemDetailsCommandDisplayName, null, null, displayItemDetailsCommand, DisplayItemDetailsCommandId);

            // Register the command within the command group.
            registrar.RegisterCommands(new[] { displayItemDetailsNamedCommand }, ItemDetailsGroupingId, Enumerable.Empty<Guid>(), Enumerable.Empty<Guid>());
        }

        private bool CanDisplayItemDetails()
        {
            if (mCommandsContext.SelectedModels.Count() != 1)
            {
                // If multiple items are selected, do not display details.
                return false;
            }

            // If the item provenance references the the subset example data
            // source, then enable the command.
            var item = mCommandsContext.SelectedModels.Single().OriginalItem;
            return GetProvenancesFromItemForExampleDataSource(item).Any(); 
        }

        private IEnumerable<ICardProvenance> GetProvenancesFromItemForExampleDataSource(IItem item)
        {
            // Use a helper method to find card provenances that come from the
            // subset example data source.
            return item.CardProvenances
                .Where(mSubsetExampleOriginHelper.OriginatesFromExampleDataSource);
        }

        private void RunDisplayItemDetailsTabIntent()
        {
            var item = mCommandsContext.SelectedModels.Single().OriginalItem;

            // If we got here, then there must be at least one card that
            // contains data from the example source, so just take the first.
            var subsettingExampleOriginIdentifier = GetProvenancesFromItemForExampleDataSource(item)
                .Select(x => mSubsettingExampleOriginIdentifierFactory.Create(x.OriginIdentifier))
                .First();

            var intent = new ItemDetailsTabIntent(subsettingExampleOriginIdentifier.Id,
                                                         subsettingExampleOriginIdentifier.Source);
            mIntentManager.Run(intent);
        }

        /// <summary>
        /// Gets an indicator of whether the command with the specified
        /// identifier should be primary - that is, whether the Intelligence
        /// Portal should display it next to the "More actions" button as well
        /// as in the drop-down and context menus.
        /// This method is called by the Intelligence Portal application.
        /// </summary>
        /// <param name="commandType">The identifier of the command in
        /// question.</param>
        public PrimaryCommandState GetPrimaryCommandDisposition(Guid commandType)
        {
            // We don't care about our command or any others being elevated to
            // primary status, so return DoNotCare.
            return PrimaryCommandState.DoNotCare;
        }

        /// <summary>
        /// Gets an indicator of whether the command with the specifed
        /// identifier should be enabled, disabled, or removed in
        /// Intelligence Portal menus.
        /// This method is called by the Intelligence Portal application after
        /// all <see cref="IContextMenuCommandProvider"/>s have registered their
        /// commands, but possibly after other providers have removed commands.
        /// </summary>
        /// <param name="commandType">The identifier of the command in
        /// question.</param>
        public CommandChange GetCommandDisposition(Guid commandType)
        {
            // We don't care about any other commands, so return None.
            return CommandChange.None;
        }

        /// <inheritdoc />
        public void Dispose()
        {
            // We don't have anything to clean up in this example.
        }
    }
}
