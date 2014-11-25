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
using System.Linq;
using i2.Apollo.Common;
using i2.Apollo.Common.Notifications;
using i2.Apollo.CommonControls;

namespace CommandExtensibilityExample
{
    /// <summary>
    /// An example class that illustrates how to add and edit the commands in
    /// the actions toolbar and context menus, in the Intelligence Portal.
    /// </summary>
    public class ExampleCommandProvider : IGroupableContextMenuCommandProvider
    {
        // Every entry in a context menu or the actions toolbar needs
        // a unique identifier.
        private static readonly Guid ExampleCommandGroupingId = Guid.Parse("{6C167DBD-D01D-450A-A028-0D9E4EE3879C}");
        private static readonly Guid ExampleCommand1Id = Guid.Parse("{9E67C7A5-C3A3-4586-A91F-C8BC94A72ED5}");
        private static readonly Guid ExampleCommand2Id = Guid.Parse("{83765750-7932-4084-8052-9383D470BD1D}");
        private static readonly Guid ExampleInsertedCommandId = Guid.Parse("{97697967-AE79-4D63-9B05-9F960D1AD902}");

        private readonly ICommandsContext mCommandsContext;
        private readonly ICommandContextState mCommandContextState;
        private readonly INotificationService mNotificationService;

        /// <summary>
        /// Initializes a new instance of the <see cref="ExampleCommandProvider"/> class.
        /// </summary>
        /// <param name="commandsContext">The context that commands run in, which is an expression of what is selected in the Intelligence Portal.</param>
        /// <param name="commandContextState">The context state, which indicates whether the actions toolbar or a context menu is being processed.</param>
        /// <param name="notificationService">The notification service, which enables commands to display messages in the Intelligence Portal.</param>
        public ExampleCommandProvider(
            ICommandsContext commandsContext, 
            ICommandContextState commandContextState, 
            INotificationService notificationService)
        {
            mCommandsContext = commandsContext;
            mCommandContextState = commandContextState;
            mNotificationService = notificationService;
        }

        /// <summary>
        /// Registers an example command group to be added to the context menu
        /// in the Intelligence Portal.
        /// This method is called by the Intelligence Portal application.
        /// </summary>
        /// <param name="registrar">The command registrar, which handles the
        /// registration of commands and command groups.</param>
        public void RegisterCommandGroupings(ICommandRegistrar registrar)
        {
            // Register the custom command group, requesting that it should be
            // displayed in the context menu after the refresh command and
            // before the default item commands. The Intelligence Portal
            // application determines the precise location of the group.
            registrar.RegisterCommandGrouping(
                commandGroupingId: ExampleCommandGroupingId, 
                groupingHeading: CommandExtensibilityExampleStringResources.GroupHeading, 
                groupingHeadingTooltip: CommandExtensibilityExampleStringResources.GroupHeadingTooltip, 
                insertBeforeGroupings: new[] { DefaultItemCommands.ItemCommandsCommandGroupingType, DefaultItemCommands.SetCommandsCommandGroupingType }, 
                insertAfterGroupings: new[] { RefreshCommandProvider.RefreshCommandGroupingType });
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
            // Register your custom commands here. You can specify where the
            // commands should appear in relation to other commands.

            // "command1" determines how many items are currently selected, and
            // displays a message that contains that information to the user.
            var command1 = new SimpleCommand
                              {
                                  IsEnabled = true,
                                  Action = () => mNotificationService.PresentAutoExpireSuccessfulActionNotificationToUser(
                                               string.Format(CommandExtensibilityExampleStringResources.ExampleCommand1MessageFormat, mCommandsContext.SelectedModels.Count()))
                              };

            var namedCommand1 = new NamedCommand(CommandExtensibilityExampleStringResources.ExampleCommand1DisplayName, 
                                                 CommandExtensibilityExampleStringResources.ExampleCommand1Tooltip, 
                                                 "/CommandExtensibilityExample;component/Assets/ExampleCommand1Icon.png", 
                                                 command1, 
                                                 ExampleCommand1Id);

            // "command2" determines whether the Intelligence Portal is
            // displaying information about a set, and reports that to the user.
            var command2 = new SimpleCommand
            {
                IsEnabled = true,
                Action = () => mNotificationService.PresentAutoExpireSuccessfulActionNotificationToUser(
                             string.Format(CommandExtensibilityExampleStringResources.ExampleCommand2MessageFormat, mCommandsContext.SetContext != null ? CommandExtensibilityExampleStringResources.ExampleCommand2PositiveResponse : CommandExtensibilityExampleStringResources.ExampleCommand2NegativeResponse))
            };

            var namedCommand2 = new NamedCommand(CommandExtensibilityExampleStringResources.ExampleCommand2DisplayName, 
                                                 CommandExtensibilityExampleStringResources.ExampleCommand2Tooltip, 
                                                 "/CommandExtensibilityExample;component/Assets/ExampleCommand2Icon.png", 
                                                 command2, 
                                                 ExampleCommand2Id);

            // The commands added here appear in the given order, within the
            // given group. "command1" therefore appears before "command2".
            // There are no existing commands in the custom group, so there is
            // no need to specify "before" or "after" information.
            registrar.RegisterCommands(
                commands: new[] { namedCommand1, namedCommand2 }, 
                containingBlock: ExampleCommandGroupingId, 
                insertBefore: Enumerable.Empty<Guid>(), 
                insertAfter: Enumerable.Empty<Guid>());

            // "insertedcommand" just displays a message to indicate that the
            // user has selected it
            var insertedCommand = new SimpleCommand
            {
                IsEnabled = mCommandsContext.SelectedModels.Any(),
                Action = () => mNotificationService.PresentAutoExpireSuccessfulActionNotificationToUser(
                             CommandExtensibilityExampleStringResources.ExampleInsertedCommandMessage)
            };

            var insertedNamedCommand = new NamedCommand(CommandExtensibilityExampleStringResources.ExampleInsertedCommandDisplayName, 
                                                        CommandExtensibilityExampleStringResources.ExampleInsertedCommandTooltip, 
                                                        "/CommandExtensibilityExample;component/Assets/InsertedCommandIcon.png", 
                                                        insertedCommand, 
                                                        ExampleInsertedCommandId);

            // Add "insertedcommand" into the list of standard commands,
            // rather than into its own group. This call requests that the
            // command is displayed after the delete command and before the
            // create link command.
            registrar.RegisterCommands(
                commands: new[] { insertedNamedCommand },
                containingBlock: DefaultItemCommands.ItemCommandsCommandGroupingType,
                insertBefore: new[] { DefaultItemCommands.DeleteCommandType },
                insertAfter: new[] { DefaultItemCommands.CreateLinkCommandType });
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
            // Make "command1" primary, which means that it appears in the
            // actions toolbar as well as in the "More actions" list.
            if (ExampleCommand1Id.Equals(commandType))
            {
                return PrimaryCommandState.IsPrimary;
            }

            // Make the standard "refresh" command not-primary in the
            // particular circumstances of browsing the contents of a set.
            if (RefreshCommandProvider.RefreshCommandType.Equals(commandType) && mCommandsContext.SetContext != null)
            {
                return PrimaryCommandState.IsNotPrimary;
            }

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
            // Decide whether to change any standard or custom commands,
            // to disable them in or remove them from the user interface.
            if (DefaultItemCommands.ExportToCsvCommandType.Equals(commandType))
            {
                // Remove the standard "export to CSV" command when any of the
                // selected items are of type "Vehicle".
                // In your code, use the identifier of the item type rather
                // than its name. The example uses the name for compatibility
                // across different schemas.
                if (mCommandsContext.SelectedModels.Any(x => x.ItemType.DisplayName.Equals("Vehicle")))
                {
                    return CommandChange.Remove;
                }
            }

            // Remove "command2" from context menus, but not from the
            // actions toolbar.
            if (ExampleCommand2Id.Equals(commandType) && mCommandContextState.Location == ContextMenuTypeLocation.ContextMenu)
            {
                return CommandChange.Remove;
            }

            // Disable the standard "delete" and "purge" commands when more
            // than one item is selected in the Intelligence Portal.
            if (DefaultItemCommands.DeleteCommandType.Equals(commandType) || DefaultItemCommands.PurgeCommandType.Equals(commandType))
            {
                if (mCommandsContext.SelectedModels.Count() > 1)
                {
                    return CommandChange.Disable;
                }
            }

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
