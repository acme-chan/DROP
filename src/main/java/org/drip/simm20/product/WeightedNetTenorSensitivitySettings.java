
package org.drip.simm20.product;

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
 * WeightedNetTenorSensitivitySettings holds the Risk Weights and Concentration Thresholds for each Risk
 *  Factor and its Tenor. The References are:
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

public class WeightedNetTenorSensitivitySettings extends
	org.drip.simm20.product.WeightedNetSensitivitySettings
{
	private double _subCurveCorrelation = java.lang.Double.NaN;
	private java.util.Map<java.lang.String, java.lang.Double> _riskWeightMap = null;
	private org.drip.measure.stochastic.LabelCorrelation _crossTenorCorrelation = null;

	/**
	 * Construct a ISDA SIMM 2.0 IR Standard Instance of WeightedNetTenorSensitivitySettings
	 * 
	 * @param currency The Currency
	 * @param subCurve The Sub-Curve
	 * 
	 * @return The ISDA SIMM 2.0 IR Standard Instance of WeightedNetTenorSensitivitySettings
	 */

	public static final WeightedNetTenorSensitivitySettings IR (
		final java.lang.String currency,
		final java.lang.String subCurve)
	{
		org.drip.simm20.rates.IRWeight irWeight = org.drip.simm20.rates.IRSettingsContainer.RiskWeight (
			currency,
			subCurve
		);

		org.drip.simm20.rates.IRThreshold irThreshold =
			org.drip.simm20.rates.IRThresholdContainer.Threshold (currency);

		try
		{
			return null == irWeight || null == irWeight ? null : new WeightedNetTenorSensitivitySettings (
				irWeight.tenorWeightMap(),
				irThreshold.deltaVega().delta(),
				org.drip.simm20.rates.IRSettingsContainer.SingleCurveTenorCorrelation(),
				org.drip.simm20.rates.IRSystemics.SINGLE_CURRENCY_CROSS_CURVE_CORRELATION
			);
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a ISDA SIMM 2.0 Credit Qualified Standard Instance of WeightedNetTenorSensitivitySettings
	 * 
	 * @param creditBucket The Credit Bucket Index
	 * 
	 * @return The ISDA SIMM 2.0 Credit Qualified Standard Instance of WeightedNetTenorSensitivitySettings
	 */

	public static final WeightedNetTenorSensitivitySettings CRQ (
		final int creditBucket)
	{
		org.drip.simm20.credit.CRBucket crBucket = org.drip.simm20.credit.CRQSettingsContainer.Bucket
			(creditBucket);

		org.drip.simm20.common.DeltaVegaThreshold deltaVegaThreshold =
			org.drip.simm20.credit.CRThresholdContainer.QualifyingThreshold (creditBucket);

		try
		{
			return null == crBucket || null == deltaVegaThreshold ? null : new
				WeightedNetTenorSensitivitySettings (
					crBucket.tenorWeightMap (org.drip.simm20.credit.CRQSettingsContainer.TenorSet()),
					deltaVegaThreshold.delta(),
					null,
					java.lang.Double.NaN
				);
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a ISDA SIMM 2.0 Credit Non Qualified Standard Instance of
	 * 		WeightedNetTenorSensitivitySettings
	 * 
	 * @param creditBucket The Credit Bucket Index
	 * 
	 * @return The ISDA SIMM 2.0 Credit Non Qualified Standard Instance of
	 * 		WeightedNetTenorSensitivitySettings
	 */

	public static final WeightedNetTenorSensitivitySettings CRNQ (
		final int creditBucket)
	{
		org.drip.simm20.credit.CRBucket crBucket = org.drip.simm20.credit.CRNQSettingsContainer.Bucket
			(creditBucket);

		org.drip.simm20.common.DeltaVegaThreshold deltaVegaThreshold =
			org.drip.simm20.credit.CRThresholdContainer.NonQualifyingThreshold (creditBucket);

		try
		{
			return null == crBucket || null == deltaVegaThreshold ? null : new
				WeightedNetTenorSensitivitySettings (
					crBucket.tenorWeightMap (org.drip.simm20.credit.CRNQSettingsContainer.TenorSet()),
					deltaVegaThreshold.delta(),
					null,
					java.lang.Double.NaN
				);
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * WeightedNetTenorSensitivitySettings Constructor
	 * 
	 * @param riskWeightMap The Risk Weight Map
	 * @param concentrationThreshold The Concentration Threshold
	 * @param crossTenorCorrelation The Cross Tenor Label Correlation
	 * @param subCurveCorrelation Sub Curve Correlation
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public WeightedNetTenorSensitivitySettings (
		final java.util.Map<java.lang.String, java.lang.Double> riskWeightMap,
		final double concentrationThreshold,
		final org.drip.measure.stochastic.LabelCorrelation crossTenorCorrelation,
		final double subCurveCorrelation)
		throws java.lang.Exception
	{
		super (concentrationThreshold);

		if (null == (_riskWeightMap = riskWeightMap) || 0 == _riskWeightMap.size() ||
			null == (_crossTenorCorrelation = crossTenorCorrelation))
		{
			throw new java.lang.Exception
				("WeightedNetTenorSensitivitySettings Constructor => Invalid Inputs");
		}

		_subCurveCorrelation = subCurveCorrelation;
	}

	/**
	 * Retrieve the Tenor Risk Weight Map
	 * 
	 * @return The Tenor Risk Weight Map
	 */

	public java.util.Map<java.lang.String, java.lang.Double> riskWeightMap()
	{
		return _riskWeightMap;
	}

	/**
	 * Retrieve the Cross Tenor Correlation
	 * 
	 * @return The Cross Tenor Correlation
	 */

	public org.drip.measure.stochastic.LabelCorrelation crossTenorCorrelation()
	{
		return _crossTenorCorrelation;
	}

	/**
	 * Retrieve the Sub-Curve Correlation
	 * 
	 * @return The Sub-Curve Correlation
	 */

	public double subCurveCorrelation()
	{
		return _subCurveCorrelation;
	}
}
