package com.zj.draw;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.zj.solution.Solution;
import com.zj.solution.Flow.NFentry;
import com.zj.network.Link;
import com.zj.network.Node;
import com.zj.network.Topology;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class JungTransformer {

	/**
	 * 拓扑集合转化成jung的图集合
	 * @param topology
	 * @return
	 */
	public static Graph<Node, Link> setTopoTransformer(Topology topology){
		Graph<Node, Link> graph = new UndirectedSparseGraph<Node, Link>();
		for(Node node : topology.getNodes()){
			graph.addVertex(node);
		}
		for(Link link : topology.getLinks()){
			graph.addEdge(link, link.getFromNode(), link.getToNode());
		}

		return graph;
	}

	/**
	 * 解决方案中流的集合转发为jung中的图集合流映射
	 * @param solution
	 * @return
	 */
	public static LinkedHashMap<Integer, ArrayList<Graph<Node, Link>>> setSoluTransformer(Solution solution){
		LinkedHashMap<Integer, ArrayList<Graph<Node, Link>>> graphMap = new LinkedHashMap<Integer,  ArrayList<Graph<Node, Link>>>();
		if(solution != null){
			//System.out.println(solution.getFlows().size());
			for(int item : solution.getFlowIdList()){
				//solution集合：flow、NFs
				ArrayList<Graph<Node, Link>> graphSolution = new ArrayList<Graph<Node, Link>>();
				//flow
				Graph<Node, Link> graphFlow = new UndirectedSparseGraph<Node, Link>();
				for(Link path : solution.getFlow(item).getPath()){
					graphFlow.addEdge(path, path.getFromNode(), path.getToNode());
				}
				graphSolution.add(graphFlow);
				//NFs
				Graph<Node, Link> graphNFs = new UndirectedSparseGraph<Node, Link>();
				for(NFentry NF : solution.getFlow(item).getNFC()){
					graphNFs.addVertex(NF.getNFnode());
				}
				graphSolution.add(graphNFs);

				graphMap.put(item, graphSolution);
			}
		}
		return graphMap;

	}

}
