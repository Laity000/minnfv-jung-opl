package com.zj.util;

import com.zj.network.Node;
import com.zj.network.Topology;

public class SortedNodesSolver {

	/**
	 * 拓扑中的节点按度数由低到高排序
	 * @param topology
	 * @param path
	 * @return
	 */
	public static int[] sortNodesByDegree(Topology topology) {
		int[] degree = new int[topology.nodesCount()];
		int[] nodeset = new int[topology.nodesCount()];
		for (int i = 0; i < topology.nodesCount(); i++) {
			Node node = topology.getNode(i);
			degree[i] = topology.getAdjNodes(node).size();
			nodeset[i] = i;
		}

		for (int i = 0; i < degree.length; i++) {
			int minIndex = 0;
			for (int j = i+1; j < degree.length; j++) {
				if (degree[j] < degree[minIndex]) {
					minIndex = j;
				}
			}
			swap(degree, i, minIndex);
			swap(nodeset, i, minIndex);
		}
		return nodeset;

	}

	private static  void swap(int[] array, int i, int j){
		if(i == j){
			return;
		}
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

}
