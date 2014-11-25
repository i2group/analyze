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
using i2.Apollo.Common.DependencyInjection;
using i2.Apollo.Controls.Views;
using System.Windows.Controls.Primitives;
using System;
using System.Linq;
using System.Windows;
using System.Windows.Controls;

namespace ExtensibilityHelper
{
    /// <summary>
    /// A utility class that provides an API for extending the Intelligence
    /// Portal with a new button in the header bar.
    /// </summary>
    public class PortalExtensibilityHelper
    {
        private readonly IDependencyInjectionContainer mContainer;

        /// <summary>
        /// Initializes a new instance of the <see cref="PortalExtensibilityHelper"/> class.
        /// </summary>
        public PortalExtensibilityHelper(
            IDependencyInjectionContainer container)
        {
            mContainer = container;
        }

        /// <summary>
        /// Adds a new button to the Intelligence Portal header bar.
        /// </summary>
        /// <param name="headerButtonCaption">The caption to display in the new button.</param>
        /// <param name="headerButtonTooltip">The text to display in the tooltip when a user hovers over the new button.</param>
        /// <param name="buttonCommand">The command to run when a user clicks the new button.</param>
        public void RegisterHeaderCommand(
            string headerButtonCaption,
            string headerButtonTooltip,
            SimpleCommand buttonCommand)
        {
            // The portal might call this method before it displays the header
            // bar. Loop until the header bar exists, or until the
            // initialization code times out while calling this method.
            var timer = mContainer.Resolve<IDispatcherTimer>();
            timer.Interval = TimeSpan.FromMilliseconds(10);
            timer.Tick += delegate
            {
                var toolboxView = Application.Current.RootVisual.GetVisualDescendants().OfType<ToolboxView>().SingleOrDefault();
                if (toolboxView == null)
                {
                    // The header bar is not yet visible.
                    return;
                }

                // The header bar is visible.
                timer.Stop();

                // Find the grid that contains the header bar buttons.
                var layoutGrid = (Grid)toolboxView.Content;
                var grid2 = (Grid)layoutGrid.Children[0];
                var grid3 = (Grid)grid2.Children[0];
                var grid4 = (Grid)grid3.Children[0];

                var destinationGrid = grid4;

                // Add a new column to the grid that contains the buttons.
                var existingColCount = destinationGrid.ColumnDefinitions.Count;
                destinationGrid.ColumnDefinitions.Add(new ColumnDefinition());

                // Add the new button to the grid.
                var button = new HeaderButton();
                button.HeaderButtonTextBlockBase.Text = headerButtonCaption;
                button.HeaderButtonTextBlockHighlight.Text = headerButtonCaption;

                var tooltip = ToolTipService.GetToolTip(button.ButtonControl) as ToolTip;
                if (tooltip != null)
                {
                    tooltip.Content = headerButtonTooltip;
                }

                button.ButtonControl.Command = buttonCommand;

                button.SetValue(Grid.ColumnProperty, existingColCount);
                destinationGrid.Children.Add(button);
            };

            timer.Start();
        }
    }
}
