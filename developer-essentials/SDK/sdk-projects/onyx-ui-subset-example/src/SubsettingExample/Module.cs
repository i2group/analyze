/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and/or initial documentation
 *******************************************************************************/
using System.Windows.Browser;
using i2.Apollo.Common;
using i2.Apollo.Common.DependencyInjection;
using i2.Apollo.Common.Notifications;
using i2.Apollo.Common.ServiceCommunication;
using i2.Apollo.CommonControls;
using i2.Apollo.Controls;
using i2.Apollo.Controls.Intent;
using i2.Apollo.Controls.ViewModels;
using i2.Apollo.SilverlightCommon;
using System;
using System.Collections.Generic;
using SubsettingExample.JsonMessage;

namespace SubsettingExample
{
    /// <summary>
    /// The startup module for the Subsetting Example.
    /// </summary>
    public class Module : IDependencyModule
    {
        /// <summary>
        /// Gets a list of the modules that this module depends on.
        /// </summary>
        public static IEnumerable<string> ModuleDependencies
        {
            get
            {
                yield return typeof(SilverlightCommonModule).FullName;
                yield return typeof(CommonModule).FullName;
                yield return typeof(ControlsModule).FullName;
            }
        }

        private readonly IDependencyInjectionContainer mContainer;
        private readonly IApplicationPhases mApplicationPhases;
        private readonly IExtensibilityHelper mExtensibilityHelper;
        private readonly IExpandableHeaderViewModel mExpandableHeaderViewModel;
        private readonly IExpandableRegionViewModel mExpandableRegionViewModel;
        private readonly IAsyncActionsFactory mAsyncActionsFactory;
        private readonly INotificationService mNotificationService;

        /// <summary>
        /// Initializes a new instance of the <see cref="Module"/> class.
        /// </summary>
        public Module(
            IDependencyInjectionContainer container, 
            IApplicationPhases applicationPhases,
            IExtensibilityHelper extensibilityHelper,
            IExpandableHeaderViewModel expandableHeaderViewModel,
            IExpandableRegionViewModel expandableRegionViewModel,
            IAsyncActionsFactory asyncActionsFactory,
            INotificationService notificationService)
        {
            mContainer = container;
            mApplicationPhases = applicationPhases;
            mExtensibilityHelper = extensibilityHelper;
            mExpandableHeaderViewModel = expandableHeaderViewModel;
            mExpandableRegionViewModel = expandableRegionViewModel;
            mAsyncActionsFactory = asyncActionsFactory;
            mNotificationService = notificationService;
        }

        /// <summary>
        /// Initializes this module. All dependent modules, as specified by
        /// <see cref="ModuleDependencies"/>, are initialized at this point.
        /// </summary>
        public void Initialize()
        {
            RegisterPhasedWork();
            RegisterFactories();
            RegisterContextCommands();
        }

        private void RegisterFactories()
        {
            mContainer.RegisterAssistedInterfaceType
                <ISubsettingExampleOriginIdentifierFactory, SubsettingExampleOriginIdentifier>();
        }

        private void RegisterPhasedWork()
        {
            // RegisterIntentHandler uses the intent manager, which is only
            // available after login. Queue this registration for the
            // RegisterSpokes phase.
            mApplicationPhases.QueueSynchronousWork(
                phase: ApplicationPhase.RegisterSpokes, 
                action: RegisterIntentHandler, 
                exceptionMapper: x => NotificationMessages.GetErrorMessage(x, NotificationMessageKeys.ApplicationInitialization));

            // Use the ExtensibilityHelper to add a button into the header bar.
            mApplicationPhases.QueueSynchronousWork(
                phase: ApplicationPhase.AfterUIPresented,
                action: CreateHeaderCommand,
                exceptionMapper: x => NotificationMessages.GetErrorMessage(x, NotificationMessageKeys.ApplicationInitialization));

            // Add an event listener to the hosting web page that uses the
            // IJsonSubsetMessageHandler to process JSON messages sent to it.
            mApplicationPhases.QueueSynchronousWork(
                phase: ApplicationPhase.AfterUIPresented,
                action: InjectPostMessageToHandlerToHostPage,
                exceptionMapper: x => NotificationMessages.GetErrorMessage(x, NotificationMessageKeys.ApplicationInitialization));

            // LoginViaBrowserStack uses the IUserCredentials, which is only
            // available after login. Queue this login for the
            // RegisterSpokes phase.
            mApplicationPhases.QueueAsynchronousWork(
                phase: ApplicationPhase.RegisterSpokes,
                action: LoginViaBrowserStack);
        }

        private void RegisterContextCommands()
        {
            var registry = mContainer.Resolve<IGenericContextMenuCommandProviderFactoryRegistry>();

            var commandsFactoryType = mContainer.CreateAssistedInterfaceFactoryType(typeof(IGenericContextMenuCommandProviderFactory<ICommandsContext>), typeof(ItemDetailsCommandProvider));
            var commandsFactory = (IGenericContextMenuCommandProviderFactory<ICommandsContext>)mContainer.Resolve(commandsFactoryType);
            registry.Register<ICommandsContext>(commandsFactory);
        }

        private void RegisterIntentHandler()
        {
            var intentManager = mContainer.Resolve<IIntentManager>();

            // Register the SubsettingTabFactory and the ItemDetailsTabFactory
            // with the intent manager, so that ISubsettingTabIntent and
            // IItemDetailsTabIntent instances are handled.
            var subsettingTabFactory = mContainer.Resolve<SubsettingTabFactory>();
            intentManager.Register(subsettingTabFactory);
            var itemDetailsTabFactory = mContainer.Resolve<ItemDetailsTabFactory>();
            intentManager.Register(itemDetailsTabFactory);
        }

        private void InjectPostMessageToHandlerToHostPage()
        {
            var jsonHandler = mContainer.Resolve<IJsonSubsetMessageHandler>();
            var htmlPage = mContainer.Resolve<IHtmlPage>();
            var locations = mContainer.Resolve<ISubsettingExampleLocations>();

            // Register an object that can handle the intents that arrive in
            // POST messages by having them processed in injected JavaScript.
            htmlPage.RegisterScriptableObject(jsonHandler.ScriptKey, jsonHandler);

            // The domain from which we expect to receive messages.
            var expectedDomain = locations
                .ExternalSubsetGenerationUri
                .GetComponents(UriComponents.SchemeAndServer, UriFormat.Unescaped);

            // Accept messages from the specified domain.
            // Use GetJavascriptContent to get our scriptable .NET object for
            // handling JSON intents, and then call handleIntent().
            //
            // Note: addEventListener only works in IE9 and above. If you need
            // to support IE8, use attachEvent instead.
            var theFunction = "window.addEventListener(\"message\",function(e){" +
                  "    if(e.origin !== \"" + expectedDomain + "\"){" +
                  "        return;" +
                  "    }" +
                  "    var messageHandler = GetJavascriptContent()[\"" + jsonHandler.ScriptKey + "\"];" +
                  "    messageHandler.handlePush(e.data);" +
                  "" +
                  "}, false);";

            HtmlPage.Window.Invoke("eval", theFunction);
        }

        private void CreateHeaderCommand()
        {
            // Create the command that the inserted header button will use.
            // When executed, the expandable region will be displayed.
            var command = new SimpleCommand
                {
                    Action = () => mExpandableHeaderViewModel.LoadView(mExpandableRegionViewModel)
                };

            // Set up the button content, and pass in the command to be used.
            mExtensibilityHelper.RegisterHeaderCommand(
                headerButtonCaption: SubsettingExampleStringResources.HeaderBarSubsettingButtonText,
                headerButtonTooltip: SubsettingExampleStringResources.HeaderBarSubsettingButtonTooltipText, 
                buttonCommand: command,
                automationSuffix: "SubsettingExample");
        }

        private void LoginViaBrowserStack(Action<Notification> callback)
        {
            mContainer.Resolve<IBrowserStackLoginHelper>().Login(callback);
        }
    }
}
