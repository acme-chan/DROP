
package org.drip.specialfunction.gamma;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2020 Lakshmi Krishnamurthy
 * Copyright (C) 2019 Lakshmi Krishnamurthy
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
 * <i>StirlingSeries</i> implements the Stirling's Series Approximation of the Gamma Functions. The
 * References are:
 * <br><br>
 * 	<ul>
 * 		<li>
 * 			Mortici, C. (2011): Improved Asymptotic Formulas for the Gamma Function <i>Computers and
 * 				Mathematics with Applications</i> <b>61 (11)</b> 3364-3369
 * 		</li>
 * 		<li>
 * 			National Institute of Standards and Technology (2018): NIST Digital Library of Mathematical
 * 				Functions https://dlmf.nist.gov/5.11
 * 		</li>
 * 		<li>
 * 			Nemes, G. (2010): On the Coefficients of the Asymptotic Expansion of n!
 * 				https://arxiv.org/abs/1003.2907 <b>arXiv</b>
 * 		</li>
 * 		<li>
 * 			Toth V. T. (2016): Programmable Calculators � The Gamma Function
 * 				http://www.rskey.org/CMS/index.php/the-library/11
 * 		</li>
 * 		<li>
 * 			Wikipedia (2019): Stirling's Approximation
 * 				https://en.wikipedia.org/wiki/Stirling%27s_approximation
 * 		</li>
 * 	</ul>
 *
 *	<br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/FunctionAnalysisLibrary.md">Function Analysis Library</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/specialfunction/README.md">Special Function Implementation Analysis</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/specialfunction/gamma/README.md">Analytic/Series/Integral Gamma Estimators</a></li>
 *  </ul>
 *
 * @author Lakshmi Krishnamurthy
 */

public class StirlingSeries extends org.drip.numerical.estimation.R1ToR1Estimator
{

	/**
	 * StirlingSeries Constructor
	 * 
	 * @param dc The Derivative Control
	 */

	public StirlingSeries (
		final org.drip.numerical.differentiation.DerivativeControl dc)
	{
		super (dc);
	}

	/**
	 * Compute the de-Moivre Term
	 * 
	 * @param x X
	 * 
	 * @return The de-Moivre Term
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double deMoivreTerm (
		final double x)
		throws java.lang.Exception
	{
		if (!org.drip.numerical.common.NumberUtil.IsValid (x) || 0. > x)
		{
			throw new java.lang.Exception ("StirlingSeries::deMoivreTerm => Invalid Inputs");
		}

		return java.lang.Math.exp (1. - x) * java.lang.Math.pow (
			x - 1.,
			x - 0.5
		);
	}

	@Override public double evaluate (
		final double x)
		throws java.lang.Exception
	{
		return java.lang.Math.sqrt (2. * java.lang.Math.PI) * deMoivreTerm (x);
	}

	@Override public org.drip.numerical.estimation.R1Estimate boundedEstimate (
		final double x)
	{
		try
		{
			double deMoivreTerm = deMoivreTerm (x);

			double estimate = java.lang.Math.sqrt (2. * java.lang.Math.PI) * deMoivreTerm;

			return new org.drip.numerical.estimation.R1Estimate (
				estimate,
				estimate,
				java.lang.Math.E * deMoivreTerm
			);
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Bounded Function Estimates along with the First Order Laplace Correction
	 * 
	 * @param x X
	 * 
	 * @return The Bounded Function Estimates along with the First Order Laplace Correction
	 */

	public org.drip.numerical.estimation.R1Estimate laplaceCorrectionEstimate (
		final double x)
	{
		java.util.TreeMap<java.lang.Integer, java.lang.Double> termWeightMap = new
			java.util.TreeMap<java.lang.Integer, java.lang.Double>();

		termWeightMap.put (
			1,
			1. / 12.
		);

		try
		{
			return seriesEstimate (
				x,
				termWeightMap,
				new org.drip.numerical.estimation.R1ToR1Series (
					org.drip.numerical.estimation.R1ToR1SeriesTerm.Asymptotic(),
					true,
					termWeightMap
				)
			);
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Bounded Function Estimates along with the Higher Order Nemes Correction
	 * 
	 * @param x X
	 * 
	 * @return The Bounded Function Estimates along with the Higher Order Nemes Correction
	 */

	public org.drip.numerical.estimation.R1Estimate nemesCorrectionEstimate (
		final double x)
	{
		java.util.TreeMap<java.lang.Integer, java.lang.Double> termWeightMap = new
			java.util.TreeMap<java.lang.Integer, java.lang.Double>();

		termWeightMap.put (
			1,
			1. / 12.
		);

		termWeightMap.put (
			2,
			1. / 288.
		);

		termWeightMap.put (
			3,
			-139. / 51840.
		);

		termWeightMap.put (
			4,
			-571. / 2488320.
		);

		try
		{
			return seriesEstimate (
				x,
				termWeightMap,
				new org.drip.numerical.estimation.R1ToR1Series (
					org.drip.numerical.estimation.R1ToR1SeriesTerm.Asymptotic(),
					true,
					termWeightMap
				)
			);
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
