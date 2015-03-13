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
using i2.Apollo.Common.Data;
using i2.Apollo.Common.DependencyInjection;
using i2.Apollo.Common.Intent;
using i2.Apollo.Controls.TabControl;
using i2.Apollo.Controls.Navigation;
using i2.Apollo.Controls.TabFramework;

namespace SubsettingExample
{
    /// <summary>
    /// The external contract for <see cref="SubsettingTabViewModel"/>.
    /// </summary>
    [ImplementedBy(typeof(SubsettingTabViewModel))]
    public interface ISubsettingTabViewModel : ITabContent
    {
    }

    /// <summary>
    /// A tab view model for handling <see cref="ISubsettingTabIntent"/>s.
    /// </summary>
    public class SubsettingTabViewModel : TabContentViewModelBase<ISubsettingTabView>, ISubsettingTabViewModel
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="SubsettingTabViewModel"/> class.
        /// </summary>
        public SubsettingTabViewModel(ISubsettingTabView view, 
            ISubsettingExampleLocations subsettingExampleLocations)
            : base(isCloseable: true)
        {
            Header = SubsettingExampleStringResources.SubsettingExampleTabHeader;
            HeaderTooltip = Header;

            PageLocation = subsettingExampleLocations.ExternalSubsetGenerationUri.ToString();

            SetAsViewModelForView(view);
        }

        /// <summary>
        /// Indicates whether this <see cref="SubsettingTabViewModel"/>
        /// can handle the specified portal intent.
        /// </summary>
        public bool CanHandleIntent(IIntent intent)
        {
            // This view model only handles intents of type
            // ISubsettingTabIntent.
            return intent is ISubsettingTabIntent;
        }

        /// <summary>
        /// Handles the specified intent.
        /// </summary>
        public void HandleIntent(IIntent intent, OpenTabOptions options)
        {
            // Handle the intent.
            var tabIntent = (ISubsettingTabIntent)intent;

            // Note: This example does not use the Data property, but in general
            // an intent can have any content, and all of that content is
            // available here.
            var intentData = tabIntent.Data;
        }

        private string mPageLocation;

        /// <summary>
        /// Gets the URL of the web page that displays the subset.
        /// </summary>
        /// <remarks>
        /// This value is bound to the HtmlSource property of the HtmlControl
        /// in SubsettingTabView.xaml, so we display the subsetting web page.
        /// </remarks>
        public string PageLocation
        {
            get { return mPageLocation; }
            private set
            {
                mPageLocation = value;
                OnPropertyChanged(this, x => x.PageLocation);
            }
        }

        #region ITabViewModel Implementation

        /// <summary>
        /// Gets the <see cref="TabType"/> for this
        /// <see cref="SubsettingTabViewModel"/>. "Unknown" is
        /// acceptable, because the tab is "unknown" as far as the standard
        /// application is concerned.
        /// </summary>
        public TabType Type
        {
            get { return TabType.Unknown; }
        }

        /// <summary>
        /// Gets the <see cref="INavigationContentEvents"/> that describe the
        /// events that can occur on this
        /// <see cref="SubsettingTabViewModel"/>, and the actions
        /// to call when those events happen.
        /// </summary>
        public INavigationContentEvents NavigationEvents
        {
            get { return null; }
        }

        /// <inheritdoc />
        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                View.Dispose();
            }

            base.Dispose(disposing);
        }

        #endregion
    }
}
