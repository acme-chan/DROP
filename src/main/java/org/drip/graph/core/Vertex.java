
package org.drip.graph.core;

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
 * <i>Vertex</i> implements a Single Vertex Node and the corresponding Egresses emanating from it. The
 * 	References are:
 * 
 * <br><br>
 *  <ul>
 *  	<li>
 *  		Bollobas, B. (1998): <i>Modern Graph Theory</i> <b>Springer</b>
 *  	</li>
 *  	<li>
 *  		Eppstein, D. (1999): Spanning Trees and Spanners
 *  			https://www.ics.uci.edu/~eppstein/pubs/Epp-TR-96-16.pdf
 *  	</li>
 *  	<li>
 *  		Gross, J. L., and J. Yellen (2005): <i>Graph Theory and its Applications</i> <b>Springer</b>
 *  	</li>
 *  	<li>
 *  		Kocay, W., and D. L. Kreher (2004): <i>Graphs, Algorithms, and Optimizations</i> <b>CRC Press</b>
 *  	</li>
 *  	<li>
 *  		Wikipedia (2020): Spanning Tree https://en.wikipedia.org/wiki/Spanning_tree
 *  	</li>
 *  </ul>
 *
 * <br><br>
 *  <ul>
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/GraphAlgorithmLibrary.md">Graph Algorithm Library</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/graph/README.md">Graph Optimization and Tree Construction Algorithms</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/graph/core/README.md">Vertexes, Edges, Trees, and Graphs</a></li>
 *  </ul>
 * <br><br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class Vertex
{
	private java.lang.String _name = "";
	private org.drip.graph.store.EdgeHeap _edgeHeap = null;
	private java.util.Map<java.lang.Double, java.lang.String> _adjacencyKeyMap = null;

	/**
	 * Vertex Constructor
	 * 
	 * @param name The Vertex Name
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public Vertex (
		final java.lang.String name)
		throws java.lang.Exception
	{
		if (null == (_name = name) || _name.isEmpty())
		{
			throw new java.lang.Exception (
				"Vertex Constructor => Invalid Inputs"
			);
		}

		_edgeHeap = new org.drip.graph.store.EdgeHeap();

		_adjacencyKeyMap = new java.util.TreeMap<java.lang.Double, java.lang.String>();
	}

	/**
	 * Retrieve the Vertex Name
	 * 
	 * @return The Vertex Name
	 */

	public java.lang.String name()
	{
		return _name;
	}

	/**
	 * Retrieve the Adjacency Key Map
	 * 
	 * @return The Adjacency Key Map
	 */

	public java.util.Map<java.lang.Double, java.lang.String> adjacencyKeyMap()
	{
		return _adjacencyKeyMap;
	}

	/**
	 * Retrieve the Edge Heap
	 * 
	 * @return The Edge Heap
	 */

	public org.drip.graph.store.EdgeHeap edgeHeap()
	{
		return _edgeHeap;
	}

	/**
	 * Retrieve the Ordered Adjacency Key List
	 * 
	 * @param descending TRUE - The Edge List is in the Descending Order of Distance
	 * 
	 * @return The Ordered Adjacency Key List
	 */

	public java.util.List<java.lang.String> adjacencyKeyList (
		final boolean descending)
	{
		return _edgeHeap.adjacencyKeyList (
			descending
		);
	}

	/**
	 * Add an Edge to the Adjacency Map
	 * 
	 * @param edge The Edge
	 * 
	 * @return TRUE - The Edge successfully added
	 */

	public java.lang.String addEdge (
		final org.drip.graph.core.Edge edge)
	{
		return _edgeHeap.addEdge (
			edge
		);
	}

	/**
	 * Remove an Edge from the Adjacency Map
	 * 
	 * @param edgeKey The Edge Key
	 * 
	 * @return TRUE - The Edge represented by the Key successfully removed
	 */

	public boolean removeEdge (
		final java.lang.String edgeKey)
	{
		return _edgeHeap.removeEdge (
			edgeKey
		);
	}

	/**
	 * Retrieve the Set of Neighbors
	 * 
	 * @return The Set of Neighbors
	 */

	public java.util.Set<java.lang.String> neighboringVertexNameSet()
	{
		return _edgeHeap.neighboringVertexNameSet();
	}

	/**
	 * Retrieve the Out-Degree of the Vertex
	 * 
	 * @return Out-Degree of the Vertex
	 */

	public int outDegree()
	{
		return _edgeHeap.outDegree();
	}

	/**
	 * Retrieve the Branching Factor of the Vertex
	 * 
	 * @return Branching Factor of the Vertex
	 */

	public int branchingFactor()
	{
		return _edgeHeap.branchingFactor();
	}
}
