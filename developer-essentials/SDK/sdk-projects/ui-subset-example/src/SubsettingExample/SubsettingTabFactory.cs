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
using i2.Apollo.Common;
using i2.Apollo.Common.Intent;
using i2.Apollo.Controls.TabFramework;

namespace SubsettingExample
{
    /// <summary>
    /// A factory for generating Intelligence Portal tabs that display
    /// subsetting information.
    /// </summary>
    public class SubsettingTabFactory : ITabFactory
    {
        private readonly IProvider<ISubsettingTabViewModel> mTabViewModelProvider;

        /// <summary>
        /// Initializes a new instance of the <see cref="SubsettingTabFactory"/> class.
        /// </summary>
        public SubsettingTabFactory(IProvider<ISubsettingTabViewModel> tabViewModelProvider)
        {
            mTabViewModelProvider = tabViewModelProvider;
        }

        /// <summary>
        /// Creates content for a tab with the specfied intent, or returns
        /// null if creation is not possible.
        /// </summary>
        /// <remarks>
        /// The intent manager calls this method when it searches for a
        /// factory that can create an <see cref="ITabContent"/> for the
        /// specified intent.
        /// </remarks>
        public ITabContent CreateContent(IIntent intent)
        {
            // This class can handle ISubsettingTabIntent instances.
            // This code opens a tab with the implementation of
            // ISubsettingTabViewModel as the view model behind the tab
            // content. HandleIntent() is then called on that view model.
            if (intent is ISubsettingTabIntent)
            {
                return mTabViewModelProvider.GetInstance();
            }

            // This tab factory cannot create content for other intents.
            return null;
        }
    }
}
