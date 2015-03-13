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
using i2.Apollo.Common.DependencyInjection;
using i2.Apollo.Common.Mvvm;
using System;

namespace SubsettingExample
{
    /// <summary>
    /// The external contract for <see cref="ItemDetailsTabView"/>.
    /// </summary>
    [ImplementedBy(typeof(ItemDetailsTabView))]
    public interface IItemDetailsTabView : IView, IDisposable
    {
    }

    /// <summary>
    /// A view for containing an <see cref="IItemDetailsTabViewModel"/>,
    /// which displays the details of items in the subsetting example.
    /// </summary>
    public partial class ItemDetailsTabView : IItemDetailsTabView
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ItemDetailsTabView"/> class.
        /// </summary>
        public ItemDetailsTabView()
        {
            InitializeComponent();
        }

        /// <inheritdoc />
        public void Dispose()
        {
            HtmlContent.Dispose();
        }
    }
}
