/*
 // Licensed Materials - Property of IBM
 // 5725-G22
 // (C) Copyright IBM Corp. 2012, 2015 All Rights Reserved.
 // US Government Users Restricted Rights - Use, duplication or
 // disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
using i2.Apollo.Common.Data;
using i2.Apollo.Common.DependencyInjection;

namespace SubsettingExample
{
    /// <summary>
    /// The external contract for <see cref="SubsetExampleOriginHelper"/>.
    /// </summary>
    [ImplementedBy(typeof(SubsetExampleOriginHelper), true)]
    public interface ISubsetExampleOriginHelper
    {
        /// <summary>
        /// Indicates whether the specified <see cref="ICardProvenance"/> is
        /// from the subsetting example.
        /// </summary>
        bool OriginatesFromExampleDataSource(ICardProvenance cardProvenance);

        /// <summary>
        /// Indicates whether the specified <see cref="IOriginIdentifier"/> is
        /// from the subsetting example.
        /// </summary>
        bool OriginatesFromExampleDataSource(IOriginIdentifier originIdentifier);
    }

    /// <summary>
    /// A helper class that can determine whether an
    /// <see cref="ICardProvenance"/> or an <see cref="IOriginIdentifier"/>
    /// comes from the subsetting example data source.
    /// </summary>
    public class SubsetExampleOriginHelper : ISubsetExampleOriginHelper
    {
        // The retrieval block key for the subsetting example data source.
        private const string OriginKey = "IAP.SDK.Example";

        /// <inheritdoc />
        public bool OriginatesFromExampleDataSource(ICardProvenance cardProvenance)
        {
            return OriginatesFromExampleDataSource(cardProvenance.OriginIdentifier);
        }

        /// <inheritdoc />
        public bool OriginatesFromExampleDataSource(IOriginIdentifier originIdentifier)
        {
            return originIdentifier.OriginType.Equals(OriginKey);
        }
    }
}