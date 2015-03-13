/*******************************************************************************
 * Copyright (c) 2015 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corp - initial API and implementation and/or initial documentation
 *******************************************************************************/

using i2.Apollo.Common.DependencyInjection;
using i2.Apollo.Common.Mvvm;

namespace SubsettingExample
{
    /// <summary>
    /// The external contract for <see cref="ExpandableRegionView"/>.
    /// </summary>
    [ImplementedBy(typeof(ExpandableRegionView))]
    public interface IExpandableRegionView : IView
    {
    }

    /// <summary>
    /// A view associated with the <see cref="ExpandableRegionViewModel"/>.
    /// </summary>
    public partial class ExpandableRegionView : IExpandableRegionView
    {
        /// <summary>
        /// Initializes a new instance of the
        /// <see cref="ExpandableRegionView"/> class.
        /// </summary>
        public ExpandableRegionView()
        {
            InitializeComponent();
        }
    }
}