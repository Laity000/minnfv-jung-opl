package com.zj.test;

import com.zj.network.Topology;
import com.zj.solution.Solution;

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
}
