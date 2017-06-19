/*******************************************************************************
 * Copyright (c) 2015 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and/or initial documentation
 ******************************************************************************/
using System;
using System.Net;
using System.Net.Browser;
using i2.Apollo.Common;
using i2.Apollo.Common.Data;
using i2.Apollo.Common.DependencyInjection;
using i2.Apollo.Common.Notifications;
using i2.Apollo.Common.ServiceCommunication;
using i2.Apollo.Common.ServiceControllers;
using i2.Apollo.Common.Services;
using i2.Apollo.Services;
using i2.Apollo.Services.ServiceHelpers;

namespace SubsettingExample
{
    /// <summary>
    /// The external contract for <see cref="BrowserStackLoginHelper"/>.
    /// </summary>
    [ImplementedBy(typeof(BrowserStackLoginHelper))]
    public interface IBrowserStackLoginHelper
    {
        /// <summary>
        /// Logs in to the Intelligence Analysis Platform using
        /// the browser stack.
        /// </summary>
        /// <param name="callback">A method that this method will call to
        /// report the success or failure of a login attempt.</param>
        void Login(Action<Notification> callback);
    }

    /// <summary>
    /// A helper class that enables login to the Intelligence Analysis Platform
    /// using the browser stack. The browser receives a cookie, which means
    /// that any iframes can access protected resources on the server.
    /// </summary>
    public class BrowserStackLoginHelper : IBrowserStackLoginHelper
    {
        private readonly IAsyncActionsFactory mAsyncActionsFactory;
        private readonly IInfoService mInfoService;
        private readonly IServiceInvoker mServiceInvoker;
        private readonly IUserCredentials mUserCredentials;
        private readonly IApolloServicesLocations mApolloServicesLocations;
        private readonly ICookieContainerSpecificWebRequestCreator mCookieContainerSpecificWebRequestCreator;

        /// <summary>
        /// Initializes a new instance of the
        /// <see cref="BrowserStackLoginHelper"/> class.
        /// </summary>
        public BrowserStackLoginHelper(IAsyncActionsFactory asyncActionsFactory,
            IInfoService infoService, IServiceInvoker serviceInvoker,
            IUserCredentials userCredentials,
            IApolloServicesLocations apolloServicesLocations,
            ICookieContainerSpecificWebRequestCreator cookieContainerSpecificWebRequestCreator)
        {
            mAsyncActionsFactory = asyncActionsFactory;
            mCookieContainerSpecificWebRequestCreator =
                cookieContainerSpecificWebRequestCreator;
            mServiceInvoker = serviceInvoker;
            mUserCredentials = userCredentials;
            mApolloServicesLocations = apolloServicesLocations;
            mInfoService = infoService;
        }

        /// <inheritdoc />
        public void Login(Action<Notification> callback)
        {
            // Temporarily register with the browser stack
            // for the info service domain.
            var infoServiceAddressString =
                mApolloServicesLocations.InfoServiceUri.ToString();
            WebRequest.RegisterPrefix(
                infoServiceAddressString, WebRequestCreator.BrowserHttp);
            var myAsyncActions = mAsyncActionsFactory.Create<IUserLoginInfo>(
                success: successAction => callback(null),
                failure: (y, z) => LoginFailure(callback, y, z));

            mServiceInvoker.CallService(
                myAsyncActions, serviceActions => mInfoService.GetUserInfo(
                    mUserCredentials.UserName, mUserCredentials.Password,
                    serviceActions), "BrowserStackLoginHelper: Login");

            // Set back to the standard.
            WebRequest.RegisterPrefix(infoServiceAddressString,
                mCookieContainerSpecificWebRequestCreator);
        }

        private static bool LoginFailure(Action<Notification> callback,
            Exception exception, bool presentedToUser)
        {
            if (!presentedToUser)
            {
                callback(NotificationMessages.GetErrorMessage(exception,
                    NotificationMessageKeys.ApplicationInitialization));
            }
            
            return true;
        }
    }
}