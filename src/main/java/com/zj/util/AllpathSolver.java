package com.zj.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import com.zj.network.Node;
import com.zj.network.Topology;

public class AllpathSolver {


	public static int[][] getAllpath(Topology topology, Node srcNode, Node destNode, int maxDelayRate){


		int maxLength =(int) Math.ceil(maxDelayRate * ShortedpathSolver.getPathLength(topology, srcNode, destNode, true));

		boolean visited[] = new boolean[topology.nodesCount()];
		Stack<Node> stack = new Stack<Node>();
		for (int i = 0; i < visited.length; i++) {
			visited[i] = false;
		}
		ArrayList<int[]> pathList = new ArrayList<int[]>();

		findPath(topology, srcNode, destNode, maxLength, visited, stack, pathList);

		return sortPath(pathList);

	}


	public static void findPath(Topology topology, Node curNode, Node destNode, int maxLength, boolean visited[], Stack<Node> stack, ArrayList<int[]> pathList){
		//时延约束
		if (stack.size()-1 >= maxLength) {
			return;
		}
		if (curNode == null || visited[curNode.getId()]) {
			return;
		}

		visited[curNode.getId()] = true;
		stack.push(curNode);
		if (curNode.equals(destNode)) {
			//打印操作
			printPath(stack, pathList);
			//回溯
			visited[curNode.getId()] = false;
			stack.pop();
			return;
		}
		ArrayList<Node> adjNodes = topology.getAdjNodes(curNode);
		for(Node adj : adjNodes){
			findPath(topology, adj, destNode, maxLength, visited, stack, pathList);
		}
		//回溯
		visited[curNode.getId()] = false;
		stack.pop();
	}

	private static void printPath(Stack<Node> stack, ArrayList<int[]> pathList){
		int numNodes = stack.size();
		int[] paths = new int[numNodes];
		Iterator<Node> iterator = stack.iterator();
		int i = 0;
		while(iterator.hasNext())
        {
            paths[i++] = iterator.next().getId();
        }
		pathList.add(paths);
	}


	private static int[][] sortPath(ArrayList<int[]> pathList){
		int numPaths = pathList.size();
		int[][] allPaths = new int[numPaths][];
		for(int i=0; i<numPaths; i++){
			int min = pathList.get(0).length;
			int index = 0;
			for(int j=0; j<pathList.size(); j++){
				int length = pathList.get(j).length;
				if(length < min){
					min = length;
					index = j;
				}
			}
			allPaths[i] = pathList.get(index);
			pathList.remove(index);
		}
		return allPaths;
	}


}
