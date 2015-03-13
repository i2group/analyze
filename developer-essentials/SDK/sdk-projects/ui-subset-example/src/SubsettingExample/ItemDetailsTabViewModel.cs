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
using i2.Apollo.Controls.Navigation;
using i2.Apollo.Controls.TabControl;
using i2.Apollo.Controls.TabFramework;

namespace SubsettingExample
{
    /// <summary>
    /// The external contract for <see cref="ItemDetailsTabViewModel"/>.
    /// </summary>
    [ImplementedBy(typeof(ItemDetailsTabViewModel))]
    public interface IItemDetailsTabViewModel : ITabContent
    {
    }

    /// <summary>
    /// A tab view model for handling <see cref="IItemDetailsTabIntent"/>s.
    /// </summary>
    /// <remarks>
    /// <see cref="TabContentViewModelBase"/> is a base class for tabs in the
    /// Intelligence Portal. That class provides the common properties and
    /// methods that are required for a view model to be used as a main tab in
    /// the application.
    /// </remarks>
    public class ItemDetailsTabViewModel : TabContentViewModelBase<IItemDetailsTabView>, IItemDetailsTabViewModel
    {
        private readonly ISubsettingExampleLocations mSubsettingExampleLocations;

        /// <summary>
        /// Initializes a new instance of the <see cref="ItemDetailsTabViewModel"/> class.
        /// </summary>
        public ItemDetailsTabViewModel(IItemDetailsTabView view,
            ISubsettingExampleLocations subsettingExampleLocations)
            : base(isCloseable: true)
        {
            mSubsettingExampleLocations = subsettingExampleLocations;

            Header = SubsettingExampleStringResources.DisplayItemDetailsTabHeader;
            HeaderTooltip = Header;

            SetAsViewModelForView(view);
        }

        /// <summary>
        /// Indicates whether this <see cref="ItemDetailsTabViewModel"/>
        /// can handle the specified portal intent.
        /// </summary>
        public bool CanHandleIntent(IIntent intent)
        {
            // This view model only handles intents of type
            // IItemDetailsTabIntent.
            return intent is IItemDetailsTabIntent;
        }

        /// <summary>
        /// Handles the specified intent.
        /// </summary>
        public void HandleIntent(IIntent intent, OpenTabOptions options)
        {
            // Handle the intent.
            var tabIntent = (IItemDetailsTabIntent)intent;
            var location = mSubsettingExampleLocations.GeneratePathToItem(tabIntent.Id, tabIntent.Source);

            ItemUrl = location.ToString();
        }

        private string mItemUrl;

        /// <summary>
        /// Gets the URL of the item details to be displayed.
        /// </summary>
        /// <remarks>
        /// This value is bound to the HtmlSource property of the HtmlControl
        /// in ItemDetailsTabView.xaml, so we display the item details
        /// page that is referred to by the portal intent.
        /// </remarks>
        public string ItemUrl
        {
            get { return mItemUrl; }
            private set
            {
                mItemUrl = value;

                // Raise a property change, so the XAML binding is refreshed.
                OnPropertyChanged(this, x => x.ItemUrl);
            }
        }

        #region ITabViewModel Implementation

        /// <summary>
        /// Gets the <see cref="TabType"/> for this
        /// <see cref="ItemDetailsTabViewModel"/>. "Unknown" is
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
        /// <see cref="ItemDetailsTabViewModel"/>, and the actions
        /// to call when those events happen.
        /// </summary>
        public INavigationContentEvents NavigationEvents
        {
            // Null is an acceptable return value, as we don't have any
            // specific actions to take when navigated to/from or closed.
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
