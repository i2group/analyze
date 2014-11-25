/*******************************************************************************
 * Copyright (c) 2014 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corp - initial API and implementation and/or initial documentation
 *******************************************************************************/

using System;
using System.Windows.Input;
using i2.Apollo.Common;
using i2.Apollo.Common.DependencyInjection;
using i2.Apollo.Common.Mvvm;
using i2.Apollo.Controls.Intent;
using i2.Apollo.Controls.ViewModels;

namespace SubsettingExample
{
    /// <summary>
    /// The external contract for <see cref="ExpandableRegionViewModel"/>.
    /// </summary>
    [ImplementedBy(typeof(ExpandableRegionViewModel), singleton: true)]
    public interface IExpandableRegionViewModel : IHeaderExpanderContent
    {
    }

    /// <summary>
    /// A view model that provides content for the expandable region in the
    /// Intelligence Portal header bar.
    /// </summary>
    public class ExpandableRegionViewModel : ViewModelFirstViewModelBase<IExpandableRegionView>, IExpandableRegionViewModel
    {
        private readonly SimpleCommand mLaunchSubsettingTabCommand;
        private readonly IProvider<IIntentManager> mIntentManagerProvider;

        /// <summary>
        /// Initializes a new instance of the <see cref="ExpandableRegionViewModel"/> class.
        /// </summary>
        public ExpandableRegionViewModel(
            IProvider<IIntentManager> intentManagerProvider,
            IExpandableRegionView view)
        {
            // Create the command that the inserted
            // expandable region button will use.
            mLaunchSubsettingTabCommand = new SimpleCommand
                             {
                                 Action = LaunchSubsettingTab
                             };

            mIntentManagerProvider = intentManagerProvider;

            SetAsViewModelForView(view);
        }

        /// <summary>
        /// Triggers the expandable region to collapse.
        /// </summary>
        private void Collapse()
        {
            this.Raise(CloseExpanderRequested);
        }

        /// <summary>
        /// Collapses the expandable region and launches the subsetting tab.
        /// </summary>
        public void LaunchSubsettingTab()
        {
            var intentManager = mIntentManagerProvider.GetInstance();
            
            Collapse();

            // Create a SubsettingTabIntent and pass it to the intent manager.
            // Because we registered SubsettingTabFactory with the intent
            // manager in Module.cs, we know that something will be able to
            // handle this intent.
            var intent = new SubsettingTabIntent("Intent Data");
            intentManager.Run(intent);
        }

        /// <summary>
        /// Returns the command that will be bound to the button in the
        /// expandable region, to allow the subsetting tab to be launched.
        /// </summary>
        public ICommand LaunchSubsettingTabCommand
        {
            get { return mLaunchSubsettingTabCommand; }
        }

        /// <summary>
        /// Occurs when an action is complete.
        /// </summary>
        public event EventHandler CloseExpanderRequested;

        /// <summary>
        /// Refreshes the contents of the expandable region.
        /// </summary>
        /// <remarks>
        /// The application calls this method before it displays the expandable
        /// region, to ensure that the content is up to date.
        /// </remarks>
        public void RefreshContents()
        {
        }
    }
}
