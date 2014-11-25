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
    /// item details.
    /// </summary>
    public class ItemDetailsTabFactory : ITabFactory
    {
        private readonly IProvider<IItemDetailsTabViewModel> mTabViewModelProvider;

        /// <summary>
        /// Initializes a new instance of the <see cref="ItemDetailsTabFactory"/> class.
        /// </summary>
        public ItemDetailsTabFactory(IProvider<IItemDetailsTabViewModel> tabViewModelProvider)
        {
            mTabViewModelProvider = tabViewModelProvider;
        }

        /// <summary>
        /// Creates content for a tab with the specified intent, or returns
        /// null if creation is not possible.
        /// </summary>
        /// <remarks>
        /// The intent manager calls this method when it searches for a
        /// factory that can create an <see cref="ITabContent"/> for the
        /// specified intent.
        /// </remarks>
        public ITabContent CreateContent(IIntent intent)
        {
            // This class can handle IItemDetailsTabIntent instances.
            // This code opens a tab with the implementation of
            // IItemDetailsTabViewModel as the view model behind the
            // tab content. HandleIntent() is then called on that view model.
            if (intent is IItemDetailsTabIntent)
            {
                return mTabViewModelProvider.GetInstance();
            }

            // This tab factory cannot create content for other intents.
            return null;
        }
    }
}
