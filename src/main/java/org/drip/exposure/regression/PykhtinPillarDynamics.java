
package org.drip.exposure.regression;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2019 Lakshmi Krishnamurthy
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * 
 *  This file is part of DROP, an open-source library targeting risk, transaction costs, exposure, margin
 *  	calculations, valuation adjustment, and portfolio construction within and across fixed income,
 *  	credit, commodity, equity, FX, and structured products.
 *  
 *  	https://lakshmidrip.github.io/DROP/
 *  
 *  DROP is composed of three modules:
 *  
 *  - DROP Analytics Core - https://lakshmidrip.github.io/DROP-Analytics-Core/
 *  - DROP Portfolio Core - https://lakshmidrip.github.io/DROP-Portfolio-Core/
 *  - DROP Numerical Core - https://lakshmidrip.github.io/DROP-Numerical-Core/
 * 
 * 	DROP Analytics Core implements libraries for the following:
 * 	- Fixed Income Analytics
 * 	- Asset Backed Analytics
 * 	- XVA Analytics
 * 	- Exposure and Margin Analytics
 * 
 * 	DROP Portfolio Core implements libraries for the following:
 * 	- Asset Allocation Analytics
 * 	- Transaction Cost Analytics
 * 
 * 	DROP Numerical Core implements libraries for the following:
 * 	- Statistical Learning
 * 	- Numerical Optimizer
 * 	- Spline Builder
 * 	- Algorithm Support
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
 * <i>PykhtinPillarDynamics</i> generates the Dynamics off of the Pillar Vertex Exposure Realizations to be
 * used in eventual Exposure Regression using the Pykhtin (2009) Scheme. The References are:
 *  
 * <br><br>
 *  	<ul>
 *  		<li>
 *  			Andersen, L. B. G., M. Pykhtin, and A. Sokol (2017): Re-thinking Margin Period of Risk
 *  				https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2902737 <b>eSSRN</b>
 *  		</li>
 *  		<li>
 *  			Andersen, L. B. G., M. Pykhtin, and A. Sokol (2017): Credit Exposure in the Presence of
 *  				Initial Margin https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2806156 <b>eSSRN</b>
 *  		</li>
 *  		<li>
 *  			Albanese, C., and L. Andersen (2014): Accounting for OTC Derivatives: Funding Adjustments and
 *  				the Re-Hypothecation Option https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2482955
 *  				<b>eSSRN</b>
 *  		</li>
 *  		<li>
 *  			Burgard, C., and M. Kjaer (2017): Derivatives Funding, Netting, and Accounting
 *  				https://papers.ssrn.com/sol3/papers.cfm?abstract_id=2534011 <b>eSSRN</b>
 *  		</li>
 *  		<li>
 *  			Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives
 *  				Pricing <i>Risk</i> <b>21 (2)</b> 97-102
 *  		</li>
 *  	</ul>
 *
 *	<br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/AnalyticsCore.md">Analytics Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ExposureAnalyticsLibrary.md">Exposure Analytics</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/exposure/README.md">Exposure</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/exposure/regression/README.md">Regression</a></li>
 *  </ul>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class PykhtinPillarDynamics
{
	private java.util.List<java.lang.Double> _exposureList = null;

	/**
	 * Construct an Instance of PykhtinPillarDynamics from the Exposure Array
	 * 
	 * @param exposureArray The Exposure Array
	 * 
	 * @return The VertexRealization Instance
	 */

	public static final PykhtinPillarDynamics Standard (
		final double[] exposureArray)
	{
		if (null == exposureArray)
		{
			return null;
		}

		java.util.List<java.lang.Double> exposureList = new java.util.ArrayList<java.lang.Double>();

		int exposureCount = exposureArray.length;

		if (0 == exposureCount)
		{
			return null;
		}

		for (double exposure : exposureArray)
		{
			if (!org.drip.quant.common.NumberUtil.IsValid (exposure))
			{
				return null;
			}

			exposureList.add (exposure);
		}

		java.util.Collections.sort (exposureList);

		try
		{
			return new PykhtinPillarDynamics (exposureList);
		}
		catch (java.lang.Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	protected PykhtinPillarDynamics (
		final java.util.List<java.lang.Double> exposureList)
		throws java.lang.Exception
	{
		if (null == (_exposureList = exposureList) || 0 == _exposureList.size())
		{
			throw new java.lang.Exception ("PykhtinPillarVertexDynamics Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Exposure Set
	 * 
	 * @return The Exposure Set
	 */

	public java.util.List<java.lang.Double> exposureList()
	{
		return _exposureList;
	}

	/**
	 * Retrieve the Pykhtin Pillar Vertex Array
	 * 
	 * @param localVolatilityGenerationControl The Local Volatility Generation Control
	 * 
	 * @return The Pykhtin Pillar Vertex Array
	 */

	public org.drip.exposure.regression.PykhtinPillar[] pillarVertexArray (
		final org.drip.exposure.regression.LocalVolatilityGenerationControl localVolatilityGenerationControl)
	{
		if (null == localVolatilityGenerationControl)
		{
			return null;
		}

		int realizationCount = _exposureList.size();

		double[] uniformCPDArray = localVolatilityGenerationControl.uniformCPDArray();

		int localVolatilityIndexShift = localVolatilityGenerationControl.localVolatilityIndexShift();

		double[] impliedBrownianVariateArray = localVolatilityGenerationControl.impliedBrownianVariateArray();

		int realizationIndex = 0;
		double[] exposureArray = new double[realizationCount];
		int localVolatilityIndexFloor = localVolatilityIndexShift;
		double[] localVolatilityArray = new double[realizationCount];
		int localVolatilityIndexCeiling = realizationCount - localVolatilityIndexShift;
		org.drip.exposure.regression.PykhtinPillar[] pillarVertexArray = new
			org.drip.exposure.regression.PykhtinPillar[realizationCount];

		for (double exposure : _exposureList)
		{
			exposureArray[realizationIndex++] = exposure;
		}

		for (int realizationCoordinate = localVolatilityIndexFloor;
			realizationCoordinate < localVolatilityIndexCeiling;
			++realizationCoordinate)
		{
			localVolatilityArray[realizationCoordinate] =
				(exposureArray[realizationCoordinate - localVolatilityIndexShift] -
					exposureArray[realizationCoordinate + localVolatilityIndexShift]) /
				(impliedBrownianVariateArray[realizationCoordinate - localVolatilityIndexShift] -
					impliedBrownianVariateArray[realizationCoordinate + localVolatilityIndexShift]);
		}

		for (int realizationCoordinate = 0;
			realizationCoordinate < localVolatilityIndexFloor;
			++realizationCoordinate)
		{
			localVolatilityArray[realizationCoordinate] = localVolatilityArray[localVolatilityIndexFloor];
		}

		for (int realizationCoordinate = localVolatilityIndexCeiling;
			realizationCoordinate < realizationCount;
			++realizationCoordinate)
		{
			localVolatilityArray[realizationCoordinate] =
				localVolatilityArray[localVolatilityIndexCeiling - 1];
		}

		for (int realizationCoordinate = 0; realizationCoordinate < realizationCount;
			++realizationCoordinate)
		{
			try
			{
				pillarVertexArray[realizationCoordinate] =
					new org.drip.exposure.regression.PykhtinPillar (
						exposureArray[realizationCoordinate],
						realizationCoordinate,
						uniformCPDArray[realizationCoordinate],
						impliedBrownianVariateArray[realizationCoordinate],
						localVolatilityArray[realizationCoordinate]
					);

				++realizationIndex;
			}
			catch (java.lang.Exception e)
			{
				e.printStackTrace();

				return null;
			}
		}

		return pillarVertexArray;
	}

	/**
	 * Generate a Local Volatility R^1 To R^1
	 * 
	 * @param localVolatilityGenerationControl The Local Volatility Generation Control
	 * @param pillarVertexArray The Array of Pykhtin Pillar Vertexes
	 * 
	 * @return The Local Volatility R^1 To R^1
	 */

	public org.drip.function.definition.R1ToR1 localVolatilityR1ToR1 (
		final org.drip.exposure.regression.LocalVolatilityGenerationControl localVolatilityGenerationControl,
		final org.drip.exposure.regression.PykhtinPillar[] pillarVertexArray)
	{
		if (null == localVolatilityGenerationControl)
		{
			return null;
		}

		int vertexCount = pillarVertexArray.length;
		double[] exposureArray = new double[vertexCount];
		double[] localVolatilityArray = new double[vertexCount];

		for (int vertexIndex = 0; vertexIndex < vertexCount; ++vertexIndex)
		{
			exposureArray[vertexIndex] = pillarVertexArray[vertexIndex].exposure();

			localVolatilityArray[vertexIndex] = pillarVertexArray[vertexIndex].localVolatility();
		}

		org.drip.spline.stretch.MultiSegmentSequence multiSegmentSequence =
			org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
				"LocalVolatilityR1ToR1_" + org.drip.quant.common.StringUtil.GUID(),
				exposureArray,
				localVolatilityArray,
				localVolatilityGenerationControl.segmentCustomBuilderControlArray(),
				null,
				org.drip.spline.stretch.BoundarySettings.NaturalStandard(),
				org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE
			);

		return null == multiSegmentSequence ? null : multiSegmentSequence.toAU();
	}

	/**
	 * Generate a Local Volatility R^1 To R^1
	 * 
	 * @param localVolatilityGenerationControl The Local Volatility Generation Control
	 * 
	 * @return The Local Volatility R^1 To R^1
	 */

	public org.drip.function.definition.R1ToR1 localVolatilityR1ToR1 (
		final org.drip.exposure.regression.LocalVolatilityGenerationControl localVolatilityGenerationControl)
	{
		return localVolatilityR1ToR1 (
			localVolatilityGenerationControl,
			pillarVertexArray (localVolatilityGenerationControl)
		);
	}
}
