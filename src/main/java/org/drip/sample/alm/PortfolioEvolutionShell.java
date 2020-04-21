
package org.drip.sample.alm;

import org.drip.alm.dynamics.AssetEvolver;
import org.drip.alm.dynamics.EvolutionDigest;
import org.drip.alm.dynamics.MatchingPortfolio;
import org.drip.alm.dynamics.MaturingAsset;
import org.drip.alm.dynamics.NonMaturingAsset;
import org.drip.alm.dynamics.SpotMarketParameters;
import org.drip.feed.loader.PropertiesParser;
import org.drip.measure.statistics.UnivariateDiscreteThin;
import org.drip.numerical.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.validation.hypothesis.ProbabilityIntegralTransform;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2020 Lakshmi Krishnamurthy
 * 
 *  This file is part of DROP, an open-source library targeting analytics/risk, transaction cost analytics,
 *  	asset liability management analytics, capital, exposure, and margin analytics, valuation adjustment
 *  	analytics, and portfolio construction analytics within and across fixed income, credit, commodity,
 *  	equity, FX, and structured products. It also includes auxiliary libraries for algorithm support,
 *  	numerical analysis, numerical optimization, spline builder, model validation, statistical learning,
 *  	and computational support.
 *  
 *  	https://lakshmidrip.github.io/DROP/
 *  
 *  DROP is composed of three modules:
 *  
 *  - DROP Product Core - https://lakshmidrip.github.io/DROP-Product-Core/
 *  - DROP Portfolio Core - https://lakshmidrip.github.io/DROP-Portfolio-Core/
 *  - DROP Computational Core - https://lakshmidrip.github.io/DROP-Computational-Core/
 * 
 * 	DROP Product Core implements libraries for the following:
 * 	- Fixed Income Analytics
 * 	- Loan Analytics
 * 	- Transaction Cost Analytics
 * 
 * 	DROP Portfolio Core implements libraries for the following:
 * 	- Asset Allocation Analytics
 *  - Asset Liability Management Analytics
 * 	- Capital Estimation Analytics
 * 	- Exposure Analytics
 * 	- Margin Analytics
 * 	- XVA Analytics
 * 
 * 	DROP Computational Core implements libraries for the following:
 * 	- Algorithm Support
 * 	- Computation Support
 * 	- Function Analysis
 *  - Model Validation
 * 	- Numerical Analysis
 * 	- Numerical Optimizer
 * 	- Spline Builder
 *  - Statistical Learning
 * 
 * 	Documentation for DROP is Spread Over:
 * 
 * 	- Main                     => https://lakshmidrip.github.io/DROP/
 * 	- Wiki                     => https://github.com/lakshmiDRIP/DROP/wiki
 * 	- GitHub                   => https://github.com/lakshmiDRIP/DROP
 * 	- Repo Layout Taxonomy     => https://github.com/lakshmiDRIP/DROP/blob/master/Taxonomy.md
 * 	- Javadoc                  => https://lakshmidrip.github.io/DROP/Javadoc/index.html
 * 	- Technical Specifications => https://github.com/lakshmiDRIP/DROP/tree/master/Docs/Internal
 * 	- Release Versions         => https://lakshmidrip.github.io/DROP/version.html
 * 	- Community Credits        => https://lakshmidrip.github.io/DROP/credits.html
 * 	- Issues Catalog           => https://github.com/lakshmiDRIP/DROP/issues
 * 	- JUnit                    => https://lakshmidrip.github.io/DROP/junit/index.html
 * 	- Jacoco                   => https://lakshmidrip.github.io/DROP/jacoco/index.html
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * <i>PortfolioEvolutionShell</i> illustrates the Execution of a Portfolio Evolution Shell. The References
 * 	are:
 *
 *	<br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/PortfolioCore.md">Portfolio Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ALMAnalyticsLibrary.md">Asset Liability Management Analytics</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/alm/README.md">Sharpe-Tint-Yotsuzuka ALM Module</a></li>
 *  </ul>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class PortfolioEvolutionShell
{

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv (
			""
		);

		String configFolder = System.getenv (
			"CONFIG"
		);

		String configFile = configFolder + "\\alm\\PortfolioEvolutionShell.txt";

		PropertiesParser propertiesParser = new PropertiesParser (
			configFile
		);

		int pathCount = propertiesParser.integerValue (
			"PathCount"
		);

		int evolutionTenorInMonths = propertiesParser.integerValue (
			"EvolutionTenorInMonths"
		);

		int maturingAssetMaturityTenor = propertiesParser.integerValue (
			"MaturingAssetMaturityTenor"
		);

		double maturingAssetInitialYield = propertiesParser.doubleValue (
			"MaturingAssetInitialYield"
		);

		double maturingAssetPriceVolatilityCoefficient = propertiesParser.doubleValue (
			"MaturingAssetPriceVolatilityCoefficient"
		);

		double maturingAssetInitialMarketValue = propertiesParser.doubleValue (
			"MaturingAssetInitialMarketValue"
		);

		double nonMaturingAssetInitialPrice = propertiesParser.doubleValue (
			"NonMaturingAssetInitialPrice"
		);

		double nonMaturingAssetAnnualReturn = propertiesParser.doubleValue (
			"NonMaturingAssetAnnualReturn"
		);

		double nonMaturingAssetAnnualVolatility = propertiesParser.doubleValue (
			"NonMaturingAssetAnnualVolatility"
		);

		double nonMaturingAssetInitialMarketValue = propertiesParser.doubleValue (
			"NonMaturingAssetInitialMarketValue"
		);

		int pitBucketCount = propertiesParser.integerValue (
			"PITBucketCount"
		);

		System.out.println ("pathCount=" + pathCount);

		System.out.println ("evolutionTenorInMonths=" + evolutionTenorInMonths);

		System.out.println ("maturingAssetMaturityTenor=" + maturingAssetMaturityTenor);

		System.out.println ("maturingAssetInitialMarketValue=" + maturingAssetInitialMarketValue);

		System.out.println ("maturingAssetInitialYield=" + maturingAssetInitialYield);

		System.out.println ("maturingAssetPriceVolatilityCoefficient=" + maturingAssetPriceVolatilityCoefficient);

		System.out.println ("nonMaturingAssetInitialMarketValue=" + nonMaturingAssetInitialMarketValue);

		System.out.println ("nonMaturingAssetInitialPrice=" + nonMaturingAssetInitialPrice);

		System.out.println ("nonMaturingAssetAnnualReturn=" + nonMaturingAssetAnnualReturn);

		System.out.println ("nonMaturingAssetAnnualVolatility=" + nonMaturingAssetAnnualVolatility);

		System.out.println ("pitBucketCount=" + pitBucketCount);

		double maturingAssetInitialPrice = Math.exp (
			-1. * maturingAssetMaturityTenor * maturingAssetInitialYield
		);

		double maturingAssetInitialPriceVolatility = maturingAssetPriceVolatilityCoefficient / Math.sqrt (maturingAssetMaturityTenor);

		SpotMarketParameters spotMarketParameters = new SpotMarketParameters (
			maturingAssetInitialPrice,
			maturingAssetInitialPriceVolatility,
			-100.,
			nonMaturingAssetInitialPrice,
			nonMaturingAssetAnnualReturn,
			nonMaturingAssetAnnualVolatility
		);

		String maturityTenor = maturingAssetMaturityTenor + "Y";
		double maturingAssetHoldings = maturingAssetInitialMarketValue / maturingAssetInitialPrice;
		double nonMaturingAssetHoldings = nonMaturingAssetInitialMarketValue / nonMaturingAssetInitialPrice;

		MatchingPortfolio matchingPortfolio = new MatchingPortfolio (
			"MATCHING PORTFOLIO",
			new MaturingAsset (
				"MATURING ASSET",
				maturingAssetHoldings,
				maturityTenor
			),
			new NonMaturingAsset (
				"NON MATURING ASSET",
				nonMaturingAssetHoldings
			)
		);

		AssetEvolver assetEvolver = new AssetEvolver (
			pathCount,
			evolutionTenorInMonths + "M",
			maturityTenor
		);

		System.out.println ("\t|------------------------------------------------||");

		System.out.println ("\t|               Simulation Set-up                ||");

		System.out.println ("\t|------------------------------------------------||");

		System.out.println ("\t| Portfolio Maturity Tenor                 => " + maturityTenor);

		System.out.println ("\t| Maturing Asset Starting Yield            => " + (100. * maturingAssetInitialYield) + "%");

		System.out.println ("\t| Maturing Asset Starting Price            => " + (100. * maturingAssetInitialPrice));

		System.out.println ("\t| Maturing Asset Starting Price Volatility => " + (100. * maturingAssetInitialPriceVolatility) + "%");

		System.out.println ("\t| Maturing Asset Initial Holdings          => " + (maturingAssetHoldings * maturingAssetInitialPrice));

		System.out.println ("\t| Non-Maturing Asset Returns               => " + (100. * nonMaturingAssetAnnualReturn) + "%");

		System.out.println ("\t| Non-Maturing Asset Volatility            => " + (100. * nonMaturingAssetAnnualVolatility) + "%");

		System.out.println ("\t| Non-Maturing Asset Initial Holdings      => " + nonMaturingAssetHoldings);

		System.out.println ("\t| Number of Simulated Paths                => " + pathCount);

		System.out.println ("\t| Evolution Tenor                          => " + evolutionTenorInMonths + "M");

		System.out.println ("\t| PIT Bucket Count                         => " + pitBucketCount);

		System.out.println ("\t|------------------------------------------------||");

		System.out.println();

		EvolutionDigest evolutionDigest = assetEvolver.simulate (
			matchingPortfolio,
			spotMarketParameters
		);

		String[] evolutionTenorArray = evolutionDigest.pathForwardTenorArray();

		String trajectoryTenor = "";

		for (String trajectoryEvolutionTenor : evolutionTenorArray)
		{
			trajectoryTenor = trajectoryTenor + trajectoryEvolutionTenor + ",";
		}

		System.out.println (trajectoryTenor);

		double[][] pathForwardPriceGrid = evolutionDigest.pathForwardPriceGrid();

		for (int pathIndex = 0; pathIndex < assetEvolver.pathCount(); ++pathIndex)
		{
			String trajectory = "";

			for (int forwardTenorIndex = 0; forwardTenorIndex < evolutionTenorArray.length; ++forwardTenorIndex)
			{
				trajectory = trajectory + FormatUtil.FormatDouble (
					pathForwardPriceGrid[forwardTenorIndex][pathIndex], 3, 8, 1., false
				) + ",";
			}

			System.out.println (trajectory);
		}

		System.out.println();

		UnivariateDiscreteThin[] univariateDiscreteThinArray = evolutionDigest.thinStatisticsArray();

		System.out.println ("\t|---------------------------------------------------------------||");

		System.out.println ("\t|            STATISTICS AT THE EVOLUTION TENOR NODES            ||");

		System.out.println ("\t|---------------------------------------------------------------||");

		System.out.println ("\t|        L -> R:                                                ||");

		System.out.println ("\t|                - Minimum                                      ||");

		System.out.println ("\t|                - Maximum                                      ||");

		System.out.println ("\t|                - Mean                                         ||");

		System.out.println ("\t|                - Error                                        ||");

		System.out.println ("\t|---------------------------------------------------------------||");

		for (int forwardPriceIndex = 0; forwardPriceIndex < evolutionTenorArray.length; ++forwardPriceIndex)
		{
			String tenorStatisticsDump = "\t| " +
				evolutionTenorArray[forwardPriceIndex] + " => " +
				FormatUtil.FormatDouble (
					univariateDiscreteThinArray[forwardPriceIndex].minimum(), 3, 8, 1., false
				) + " | " +
				FormatUtil.FormatDouble (
					univariateDiscreteThinArray[forwardPriceIndex].maximum(), 3, 8, 1., false
				) + " | " +
				FormatUtil.FormatDouble (
					univariateDiscreteThinArray[forwardPriceIndex].average(), 3, 8, 1., false
				) + " | " +
				FormatUtil.FormatDouble (
					univariateDiscreteThinArray[forwardPriceIndex].error(), 1, 8, 1., false
				) + " ||";

			System.out.println (tenorStatisticsDump);
		}

		System.out.println ("\t|----------------------------------------------------------------||");

		System.out.println();

		System.out.println ("\t|---------------------------------------------------------------||");

		System.out.println ("\t|    PERCENTILE TEST STATISTIC AT THE EVOLUTION TENOR NODES     ||");

		System.out.println ("\t|---------------------------------------------------------------||");

		System.out.println ("\t|        L -> R:                                                ||");

		System.out.println ("\t|                - Tenor Node, Test Statistic Percentiles       ||");

		System.out.println ("\t|---------------------------------------------------------------||");

		ProbabilityIntegralTransform[] pitArray = evolutionDigest.pitArray();

		double pitIncrement = 1. / pitBucketCount;

		for (int pitIndex = 1;
			pitIndex < pitArray.length;
			++pitIndex)
		{
			String pitRow = "\t" + evolutionTenorArray[pitIndex] + ",";

			for (int pitBucketIndex = 0;
				pitBucketIndex <= pitBucketCount;
				++pitBucketIndex)
			{
				pitRow = pitRow + pitArray[pitIndex].testStatistic (
					pitIncrement * pitBucketIndex
				) + ",";
			}

			System.out.println (pitRow);
		}

		System.out.println ("\t|---------------------------------------------------------------||");

		EnvManager.TerminateEnv();
	}
}
