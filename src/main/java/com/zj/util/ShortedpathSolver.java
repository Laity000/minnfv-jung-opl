package com.zj.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.zj.network.Node;
import com.zj.network.Topology;

public class ShortedpathSolver {

	private static int[][][] pathCache = null;

	public static int getPathLength(Topology topology, Node srcNode, Node destNode, boolean isCache) {
		if (isCache) {
			if(pathCache == null){
				getAllPath(topology);
			}
			return pathCache[srcNode.getId()][destNode.getId()].length-1;
		}else {
			return BFSsearch(topology, srcNode, destNode).length-1;
		}
	}

	public static int[] getPath(Topology topology, Node srcNode, Node destNode, boolean isCache){
		if (isCache) {
			if (pathCache == null) {
				getAllPath(topology);
			}
			return pathCache[srcNode.getId()][destNode.getId()];
		}else {
			return BFSsearch(topology, srcNode, destNode);
		}

	}

	private static void getAllPath(Topology topology){
		int numNode = topology.nodesCount();
		pathCache = new int[numNode][numNode][];
		for(int i = 0; i < numNode; i++){
			for (int j = 0; j < numNode; j++) {
				pathCache[i][j] = BFSsearch(topology, topology.getNode(i), topology.getNode(j));
			}
		}
	}



	private static int[] BFSsearch(Topology topology, Node srcNode, Node destNode){
		int[] path = null;
		if(srcNode.equals(destNode)){
			path = new int[1];
			path[0] = srcNode.getId();
		}else {

			int numNode = topology.nodesCount();
			//注意节点id从0开始
			boolean[] visited = new boolean[numNode];
			//长度
			int[] len = new int[numNode];
			//路径
			int[] dist = new int[numNode];
			for (int i = 0; i < numNode; i++) {
				visited[i] = false;
				len[i] = 0;
				dist[i] = -1;
			}
			Queue<Node> queue = new LinkedList<Node>();
			int srcNodeID = srcNode.getId();
			visited[srcNodeID] = true;
			queue.offer(srcNode);
			while(!queue.isEmpty()){
				Node node = queue.poll();
				ArrayList<Node> adjNodes = topology.getAdjNodes(node);
				for(Node adj : adjNodes){
					if (visited[adj.getId()] == false) {
						visited[adj.getId()] = true;
						len[adj.getId()] = len[node.getId()] + 1;
						dist[adj.getId()] = node.getId();
						if(adj.equals(destNode)){
							int order = adj.getId();
							path = new int[len[order] + 1];
							int i = len[order];
							path[i--] = order;
							while(dist[order] != -1){
								order = dist[order];
								path[i--] = order;
							}
						return path;
						}
						queue.offer(adj);
					}
				}
			}


		}
		return path;

	}

}
