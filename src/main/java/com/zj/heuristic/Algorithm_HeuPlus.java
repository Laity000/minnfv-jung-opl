package com.zj.heuristic;

import com.zj.network.Demands;
import com.zj.network.Node;
import com.zj.network.Topology;
import com.zj.solution.Solution;
import com.zj.solution.VM;

public class Algorithm_HeuPlus {


	public static Solution start(Topology topo, Demands dems) {

		Solution solu = Algorithm_Heu.installNFC(topo, dems);
		Solution preSolu = null;
		Node banedNode = null;
		//禁用未初始化的节点
		for(VM vm : solu.getVMs()){
			if(!vm.hasVM()){
				topo.getNode(vm.getNode().getId()).setBanFlag(true);
			}
		}
		do {
			//在已初始化的节点中寻找可以被禁用的节点，满足条件：①VMsum最小，②度最小
			banedNode = findBanedNode(topo, solu);
			if (banedNode != null) {
				topo.getNode(banedNode.getId()).setBanFlag(true);
			}
			preSolu = Algorithm_Heu.installNFC(topo, dems);
		} while (preSolu.getFlows().size() == dems.getDemands().size());
		//回溯
		if (banedNode != null) {
			topo.getNode(banedNode.getId()).setBanFlag(false);
			preSolu = Algorithm_Heu.installNFC(topo, dems);
		}
		return preSolu;


	}

	/**
	 *
	 * @param topo
	 * @param solu
	 * @return
	 */
	private static Node findBanedNode(Topology topo, Solution solu) {
		Node banedNode = null;
		for(Node node : topo.getNodes()){
			if (!node.getBanFlag()) {
				if (banedNode == null) {
					banedNode = node;
				}else {
					int banedVMNum  = solu.getVM(node.getId()).getVMsum();
					int nodeVMNum = solu.getVM(node.getId()).getVMsum();
					if(nodeVMNum < banedVMNum){
						banedNode = node;
					}else if (nodeVMNum == banedVMNum) {
						int banedDegree = topo.getAdjNodes(banedNode).size();
						int nodeDegree = topo.getAdjNodes(node).size();
						if(nodeDegree < banedDegree){
							banedNode = node;
						}
					}
				}
			}
		}
		return banedNode;

	}


	//
	//Algorithm.removeVM(topology, demands, solution, new int[]{0,1,2,3,4,5,6,7,8,10});

}
