
package org.drip.sample.simm20estimates;

import java.util.HashMap;
import java.util.Map;

import org.drip.quant.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.simm20.margin.CTBucketNetSensitivity;
import org.drip.simm20.margin.CTNetSensitivity;
import org.drip.simm20.parameters.CTSensitivitySettings;
import org.drip.simm20.product.CTSensitivity;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for buy/side financial/trading model
 *  	libraries targeting analysts and developers
 *  	https://lakshmidrip.github.io/DRIP/
 *  
 *  DRIP is composed of four main libraries:
 *  
 *  - DRIP Fixed Income - https://lakshmidrip.github.io/DRIP-Fixed-Income/
 *  - DRIP Asset Allocation - https://lakshmidrip.github.io/DRIP-Asset-Allocation/
 *  - DRIP Numerical Optimizer - https://lakshmidrip.github.io/DRIP-Numerical-Optimizer/
 *  - DRIP Statistical Learning - https://lakshmidrip.github.io/DRIP-Statistical-Learning/
 * 
 *  - DRIP Fixed Income: Library for Instrument/Trading Conventions, Treasury Futures/Options,
 *  	Funding/Forward/Overnight Curves, Multi-Curve Construction/Valuation, Collateral Valuation and XVA
 *  	Metric Generation, Calibration and Hedge Attributions, Statistical Curve Construction, Bond RV
 *  	Metrics, Stochastic Evolution and Option Pricing, Interest Rate Dynamics and Option Pricing, LMM
 *  	Extensions/Calibrations/Greeks, Algorithmic Differentiation, and Asset Backed Models and Analytics.
 * 
 *  - DRIP Asset Allocation: Library for model libraries for MPT framework, Black Litterman Strategy
 *  	Incorporator, Holdings Constraint, and Transaction Costs.
 * 
 *  - DRIP Numerical Optimizer: Library for Numerical Optimization and Spline Functionality.
 * 
 *  - DRIP Statistical Learning: Library for Statistical Evaluation and Machine Learning.
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
 * CTDeltaMargin illustrates the Computation of the CT Delta Margin for across a Group of Commodity Bucket
 *  Exposure Sensitivities. The References are:
 *  
 *  - Andersen, L. B. G., M. Pykhtin, and A. Sokol (2017): Credit Exposure in the Presence of Initial Margin,
 *  	https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2806156, eSSRN.
 *  
 *  - Albanese, C., S. Caenazzo, and O. Frankel (2017): Regression Sensitivities for Initial Margin
 *  	Calculations, https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2763488, eSSRN.
 *  
 *  - Anfuso, F., D. Aziz, P. Giltinan, and K. Loukopoulus (2017): A Sound Modeling and Back-testing
 *  	Framework for Forecasting Initial Margin Requirements,
 *  	https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2716279, eSSRN.
 *  
 *  - Caspers, P., P. Giltinan, R. Lichters, and N. Nowaczyk (2017): Forecasting Initial Margin Requirements
 *  	- A Model Evaluation https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2911167, eSSRN.
 *  
 *  - International Swaps and Derivatives Association (2017): SIMM v2.0 Methodology,
 *  	https://www.isda.org/a/oFiDE/isda-simm-v2.pdf.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CTDeltaMargin
{

	private static final Map<Integer, Double> TenorSensitivityMap (
		final double[] tenorSensitivities)
		throws Exception
	{
		Map<Integer, Double> oisTenorSensitivities = new HashMap<Integer, Double>();

		for (int bucketIndex = 1; bucketIndex <= tenorSensitivities.length; ++bucketIndex)
		{
			oisTenorSensitivities.put (
				bucketIndex,
				tenorSensitivities[bucketIndex - 1]
			);
		}

		return oisTenorSensitivities;
	}

	public static final void main (
		final String[] inputs)
		throws Exception
	{
		EnvManager.InitEnv ("");

		double notionalScaler = 100.;

		Map<Integer, Double> bucketSensitivityMap = TenorSensitivityMap (
			new double[]
			{
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5),
				notionalScaler * (Math.random() - 0.5)
			}
		);

		CTSensitivitySettings commoditySensitivitySettings = CTSensitivitySettings.ISDA();

		CTNetSensitivity commodityNetSensitivity = new CTSensitivity (bucketSensitivityMap).deltaMargin
			(commoditySensitivitySettings);

		System.out.println ("\t||---------------------------------------------||");

		System.out.println ("\t||      COMMODITY BUCKET NET SENSITIVITY       ||");

		System.out.println ("\t||---------------------------------------------||");

		System.out.println ("\t||                                             ||");

		System.out.println ("\t||    L -> R:                                  ||");

		System.out.println ("\t||          - Bucket Index                     ||");

		System.out.println ("\t||          - Weighted Net Sensitivity         ||");

		System.out.println ("\t||          - Concentration Factor             ||");

		System.out.println ("\t||          - Weighted/Adjusted Sensitivity    ||");

		System.out.println ("\t||          - Margin Variance                  ||");

		System.out.println ("\t||---------------------------------------------||");

		for (Map.Entry<Integer, CTBucketNetSensitivity> netSensitivityEntry :
			commodityNetSensitivity.bucketMap().entrySet())
		{
			System.out.println (
				"\t|| " +
				FormatUtil.FormatDouble (netSensitivityEntry.getKey(), 2, 0, 1.) + " | " +
				FormatUtil.FormatDouble (netSensitivityEntry.getValue().weighted(), 4, 0, 1.) + " | " +
				FormatUtil.FormatDouble (netSensitivityEntry.getValue().concentrationFactor(), 2, 3, 1.) + " | " +
				FormatUtil.FormatDouble (netSensitivityEntry.getValue().weightedAndAdjusted(), 5, 0, 1.) + " | " +
				FormatUtil.FormatDouble (netSensitivityEntry.getValue().marginVariance(), 9, 0, 1.) + " ||"
			);
		}

		System.out.println ("\t||---------------------------------------------||");

		System.out.println();

		System.out.println ("\t||------------------------------------||");

		System.out.println (
			"\t|| Commodity Delta Margin => " +
			FormatUtil.FormatDouble (commodityNetSensitivity.deltaMargin (commoditySensitivitySettings), 5, 1, 1.) + " ||"
		);

		System.out.println ("\t||------------------------------------||");

		EnvManager.TerminateEnv();
	}
}
