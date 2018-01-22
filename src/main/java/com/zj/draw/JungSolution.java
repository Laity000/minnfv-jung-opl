package com.zj.draw;

import java.util.LinkedHashMap;

import com.zj.network.Link;
import com.zj.network.Node;

import edu.uci.ics.jung.graph.Graph;

public class JungSolution {
	/*
	 *
	 */
	LinkedHashMap<Integer, Graph<Node, Link>> JFlowPath = new LinkedHashMap<Integer, Graph<Node,Link>>();
	/*
	 *
	 */
	LinkedHashMap<Integer, Graph<Node, Link>> JFlowNFC = new LinkedHashMap<Integer, Graph<Node,Link>>();
	/*
	 *
	 */
	LinkedHashMap<Integer, String> JFlowInfo = new LinkedHashMap<Integer, String>();


}
