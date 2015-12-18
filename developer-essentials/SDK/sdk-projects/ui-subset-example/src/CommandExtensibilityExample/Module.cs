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
using System.Collections.Generic;
using i2.Apollo.Common;
using i2.Apollo.Common.DependencyInjection;
using i2.Apollo.CommonControls;
using i2.Apollo.Controls;
using i2.Apollo.SilverlightCommon;

namespace CommandExtensibilityExample
{
    /// <summary>
    /// The startup module for the Command Extensibility Example.
    /// The Intelligence Portal application looks for assemblies that support
    /// <see cref="IDependencyModule"/>. The application ensures that
    /// dependencies specified in <see cref="ModuleDependencies"/> are loaded
    /// before it calls Initialize.
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
                yield return typeof(ApolloCommonControlsModule).FullName;
                yield return typeof(SilverlightCommonModule).FullName;
                yield return typeof(CommonModule).FullName;
                yield return typeof(ControlsModule).FullName;
            }
        }

        private readonly IDependencyInjectionContainer mContainer;

        /// <summary>
        /// Initializes a new instance of the <see cref="Module"/> class.
        /// </summary>
        public Module(IDependencyInjectionContainer container)
        {
            mContainer = container;
        }

        /// <summary>
        /// Initializes this module. All dependent modules, as specified by
        /// <see cref="ModuleDependencies"/>, are initialized at this point.
        /// </summary>
        public void Initialize()
        {
            RegisterContextCommands();
        }

        private void RegisterContextCommands()
        {
            // Ask unity to give us the central registry for holding on to
            // context menu command provider factories.
            var registry = mContainer.Resolve<IGenericContextMenuCommandProviderFactoryRegistry>();

            // Create an assisted implementation of the context menu command
            // provider factory, for producing ExampleCommandProvider factory
            // instances.
            var commandsFactoryType = mContainer.CreateAssistedInterfaceFactoryType(
                                                       factoryInterfaceType: typeof(IGenericContextMenuCommandProviderFactory<ICommandsContext>), 
                                                       implementationType: typeof(ExampleCommandProvider));

            // Create an instance of our ExampleCommandProvider factory.
            var commandsFactory = (IGenericContextMenuCommandProviderFactory<ICommandsContext>)mContainer.Resolve(commandsFactoryType);

            // Register our factory with the central registry.
            registry.Register<ICommandsContext>(commandsFactory);
        }
    }
}
