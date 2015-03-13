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
    /// The external contract for <see cref="SubsettingTabView"/>.
    /// </summary>
    [ImplementedBy(typeof(SubsettingTabView))]
    public interface ISubsettingTabView : IView, IDisposable
    {
        /// <summary>
        /// Send some data to the HTML content that is hosted in this view.
        /// </summary>
        void PostMessage(string serializedMessage, string targetOrigin);
    }

    /// <summary>
    /// A view for an <see cref="ISubsettingTabViewModel"/>.
    /// </summary>
    public partial class SubsettingTabView : ISubsettingTabView
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="SubsettingTabView"/> class.
        /// </summary>
        public SubsettingTabView()
        {
            InitializeComponent();
        }

        /// <inheritdoc />
        public void Dispose()
        {
            HtmlContent.Dispose();
        }

        /// <inheritdoc />
        public void PostMessage(string serializedMessage, string targetOrigin)
        {
            HtmlContent.PostMessage(serializedMessage, targetOrigin);
        }
    }
}
