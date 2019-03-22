package com.zj.test;

import com.zj.network.Demand;
import com.zj.network.Demands;
import com.zj.network.Topology;
import com.zj.solution.Solution;
import com.zj.util.ShortedpathSolver;

public class Test1 {
	/**
	 * 测试名称
	 */
	private String name;
	/**
	 * 拓扑图
	 */
	private Topology topology;

	private Solution solution;

	public Test1() {
		this("test1");
	}

	public Test1(String name) {

		this.name = name;
		init();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Topology getTopology() {
		return topology;
	}


	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}

	private void init(){
		topology = new Topology("topo_minlink");
		//新建节点
		for(int i=0; i<7; i++){
			topology.newNode(i);
		}
		//新建链路
		topology.newLink(0, 1, 12);
		topology.newLink(0, 2, 12);
		topology.newLink(1, 3, 12);
		topology.newLink(2, 4, 5 );
		topology.newLink(2, 5, 5 );
		topology.newLink(2, 6, 5 );
		topology.newLink(3, 4, 12);
		topology.newLink(4, 5, 4);
		topology.newLink(5, 6, 4);

	}
	
	public static int getlengh(Topology topo, Demands demands) {
		int[] a = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80};
		int leng = 0;
		for (int i = 0; i < a.length; i++) {
			Demand dem = demands.getDemand(a[i]);
			leng += ShortedpathSolver.getPathLength(topo, dem.getSrcNoed(), dem.getDestNode(), false);
		}
		return leng;
		
	}

}
