package com.zj.data;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.zj.data.JsonBean.JsonArcs;
import com.zj.data.JsonBean.JsonDemand;
import com.zj.data.JsonBean.JsonFlow;
import com.zj.data.JsonBean.JsonTempOrdinal;
import com.zj.draw.JungDrawer;
import com.zj.draw.JungTransformer;
import com.zj.ga.GAsolver;
import com.zj.heuristic.Algorithm;
import com.zj.heuristic.Algorithm3;
import com.zj.network.Demand;
import com.zj.network.Demands;
import com.zj.network.Link;
import com.zj.network.NFtype;
import com.zj.network.Node;
import com.zj.network.Topology;
import com.zj.solution.Flow;
import com.zj.solution.Solution;
import com.zj.solution.VM;
import com.zj.solution.VM.SFentry;
import com.zj.util.AllpathSolver;

/**
 *
 * @Title: JsonParser.java
 * @Description: TODO 解析cplex-opl运行后生成的json形式的数据
 * @author ZhangJing
 *
 */

public class JsonParser {

	public void loadHeuJsonData(String pathname) {
		Gson gson = new Gson();
		JsonReader reader;
		JsonBean jsonBean = null;
		try {

			reader = new JsonReader(new FileReader(pathname));
		    jsonBean = gson.fromJson(reader, JsonBean.class);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("json文件导入有误！请检查文件地址。");
		}

		Topology topology = parseTopo(jsonBean);
		Demands demands = parseDems(jsonBean, topology);

		//Algorithm.banVMs(topology, new int[]{0,1,2,3,4,5,6,7,8,10});

		Solution solution =Algorithm.installNFC(topology, demands);
		//Solution solution = Algorithm3.start(topology, demands);
		//Solution solution = new GAsolver(topology, demands).run();
		showGraph(topology, solution);
		printInfo(topology, demands, solution);
		//
		//Algorithm.removeVM(topology, demands, solution, new int[]{0,1,2,3,4,5,6,7,8,10});
		//Algorithm.updateNFC(solution, topology, demands);


		/*
		int[][] paths = AllpathSolver.getAllpath(topology, topology.getNode(7), topology.getNode(8));
		for(int i=0; i<paths.length; i++){
			for(int j=0; j<paths[i].length; j++){
				System.out.print(paths[i][j] + "->");
			}
			//System.out.println(Algorithm.updatePreNFC(0, solution, demands.getDemand(85), paths[i]));

		}
		*/
	}

	public void loadHeuPlusJsonData(String pathname) {
		Gson gson = new Gson();
		JsonReader reader;
		JsonBean jsonBean = null;
		try {

			reader = new JsonReader(new FileReader(pathname));
		    jsonBean = gson.fromJson(reader, JsonBean.class);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("json文件导入有误！请检查文件地址。");
		}

		Topology topology = parseTopo(jsonBean);
		Demands demands = parseDems(jsonBean, topology);

		Solution solution = Algorithm3.start(topology, demands);

		showGraph(topology, solution);
		printInfo(topology, demands, solution);
	}

	public Solution loadGAJsonData(String pathname) {
		Gson gson = new Gson();
		JsonReader reader;
		JsonBean jsonBean = null;
		try {

			reader = new JsonReader(new FileReader(pathname));
		    jsonBean = gson.fromJson(reader, JsonBean.class);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("json文件导入有误！请检查文件地址。");
		}

		Topology topology = parseTopo(jsonBean);
		Demands demands = parseDems(jsonBean, topology);
		Solution solution = new GAsolver(topology, demands).run();
		showGraph(topology, solution);
		printInfo(topology, demands, solution);
		return solution;

	}



	public void loadOplJsonData(String pathname) {
		Gson gson = new Gson();
		JsonReader reader;
		JsonBean jsonBean = null;
		try {

			reader = new JsonReader(new FileReader(pathname));
		    jsonBean = gson.fromJson(reader, JsonBean.class);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("json文件导入有误！请检查文件地址。");
		}

		Topology topology = parseTopo(jsonBean);
		Demands demands = parseDems(jsonBean, topology);
		Solution solution = parseSolu(jsonBean, topology, demands, pathname);
		showGraph(topology, solution);
		printInfo(topology, demands, solution);

		/*
		int[][] paths = AllpathSolver.getAllpath(topology, topology.getNode(0), topology.getNode(1));
		for(int i=0; i<paths.length; i++){
			for(int j=0; j<paths[i].length; j++){
				System.out.print(paths[i][j] + "->");
			}
			System.out.println(Algorithm.createPreNFC(solution, demands.getDemand(1), paths[i]));

		}
		for(int i=0; i<paths.length; i++){
			for(int j=0; j<paths[i].length; j++){
				System.out.print(paths[i][j] + "->");
			}

			System.out.println(Algorithm.updatePreNFC(0, solution, demands.getDemand(1), paths[i]));
		}
		*/

		//Algorithm.getIdleSFsList(0, 3);

	}

	private Topology parseTopo(JsonBean jsonBean){
		/*
		 * 新建拓扑
		 */
		Topology topology = new Topology("topo_minnfv");
		//新建节点,从0开始
		for(int i=0; i<jsonBean.getNumNode(); i++){
			if(jsonBean.getNodeWeight() == null) {
				topology.newNode(i);
			}else {
				topology.newNode(i, jsonBean.getNodeWeight().get(i));
			}

		}
		//新建链路
		for(JsonArcs arcs : jsonBean.getArcs()){
			topology.newLink(arcs.getFromNodeID(), arcs.getToNodeID(), arcs.getCapacity());
		}
		return topology;
	}

	private Demands parseDems(JsonBean jsonBean, Topology topology){
		/*
		 * 新建需求集，默认时延
		 */
		//Demands demands = new Demands("dems_minnfv", topology.nodesCount());
		//Demands demands = new Demands("dems_minnfv", 10);
		Demands demands = new Demands("dems_minnfv");
		for(JsonDemand jsonDemand : jsonBean.getDemands()){
			int demandid = jsonDemand.getId();
			Node srcNode = topology.getNode(jsonDemand.getSrcNodeID());
			Node destNode = topology.getNode(jsonDemand.getDestNodeID());
			float supply = jsonDemand.getSupply();
			int[] SFC = jsonDemand.getSFC();
			demands.newDemand(demandid, srcNode, destNode, supply, SFC, topology);
		}
		return demands;
	}



	private Solution parseSolu(JsonBean jsonBean, Topology topology, Demands demands, String pathname){
		String[] splitstr = pathname.split("\\\\|\\.");
		String soluname = splitstr[splitstr.length - 2];

		/*
		 * 新建解决方案
		 */
		Solution solution = new Solution(soluname, demands.demandsCount());
		//新建流,添加流的需求信息
		for(Demand demand : demands.getDemands()){
			solution.newFlow(demand.getId(), demand.getSrcNoed(),
					demand.getDestNode(), demand.getSupply(), demand.getShortedpathLength());
		}
		//添加流的路径信息(这里的jsonflow是链路形式)
		for(JsonFlow jsonFlow : jsonBean.getFlows()){
			int fromid = jsonFlow.getFromNodeID();
			int toid = jsonFlow.getToNodeID();
			Link path = topology.getLink(fromid, toid);
			// 检查流的路径是否与拓扑的链路反向
			if (path == null) {
				path = topology.getLink(toid, fromid);
				if (path == null) {
					throw new IllegalStateException(
							"this flow is non-existent in topology." + fromid + "/ toid" + toid);
				}
			}
			solution.addFlow(jsonFlow.getDemandID(), path);
		}
		//新建流的NFC和节点的VM
		for(JsonTempOrdinal ordinal : jsonBean.getTempOrdinals()){
			int flowid = ordinal.getDemandID();
			Flow flow = solution.getFlow(flowid);
			Node node = topology.getNode(ordinal.getNodeID());
			NFtype nf = NFtype.valueOf(ordinal.getNFString());
			//添加流的NFC信息
			solution.addFlow(flowid, nf, node, ordinal.getOrdinal());
			//新建节点VM的各个NFs
			solution.addVM(node, nf, flow);
		}

		return solution;
	}

	private void showGraph(Topology topology, Solution solution) {
		JungDrawer<Node, Link> jd = new JungDrawer<Node, Link>(JungTransformer.setTopoTransformer(topology),
				JungTransformer.setSoluTransformer(solution));
		jd.setTitle(solution.getName());
		jd.initDraw();

	}

	private void printInfo(Topology topology, Demands demands, Solution solution){
		/*
		for(Node node : topology.getNodes()){
			System.out.println("Node [_id=" + node.getId() + ", + _VM=" + node.getVMinfo() + "]");
		}
		*/
		int numActivedNodes = 0;
		int numACtivedVMs = 0;
		for(VM vm :solution.getVMs()){
			System.out.println(vm);
			if (vm.hasVM()) {
				numActivedNodes++;
				numACtivedVMs += vm.getVMsum();
			}
		}

		/*
		for(Flow flow : solution.getFlows()){
			System.out.println(flow);
		}
		*/
		for(Demand dem : demands.getDemands()){
			Flow flow = solution.getFlow(dem.getId());
			if (flow == null) {
				System.out.println("Flow [_id=" + dem.getId() +
						" ,_src/dest=" + dem.getSrcNoed().getId() + "/" + dem.getDestNode().getId() +
						" ,_spLen="+ dem.getShortedpathLength() + "] is null!");
			}else {
				System.out.println(flow);
			}
		}

		System.out.println("\nNumActivedNodes=" + numActivedNodes);
		System.out.println("\nNumActivedVMs=" + numACtivedVMs);

		System.out.println("\nAvgSPLen=" + solution.getAvgSPLen());
		System.out.println("\nAvgCPLen=" + solution.getAvgCPLen());

		System.out.println("\nActiveEnergy=" + solution.getActiveEnergy() + "W");

		System.out.println("\nFitValue=" + solution.getFitValue());
	}


}
