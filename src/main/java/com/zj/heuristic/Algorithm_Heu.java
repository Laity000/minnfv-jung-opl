package com.zj.heuristic;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
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

public class Algorithm_Heu {


	public static Solution installNFC(Topology topo, Demands demands) {
		Solution solu = new Solution("H_Solu", demands.demandsCount());
		createVM(topo, solu);
		for (Demand dem : demands.getDemands()) {
			// 路径集根据最短优先级已排序
			int[][] paths = AllpathSolver.getAllpath(topo, dem.getSrcNoed(), dem.getDestNode(), demands.getMaxDelayRate());
			for (int i = 0; i <= dem.SFCCount(); i++) {
				boolean flag = false;
				for (int j = 0; j < paths.length; j++) {
					// 生成指定路径上的预安装NFC
					LinkedHashMap<NFtype, Node> preNFC = updatePreNFC(i, topo, solu, dem, paths[j]);
					// TODO:判断该路径是否可以安装NFC
					if (preNFC.size() == dem.SFCCount()) {
						// TODO:可以安装
						createFlow(topo, solu, dem, paths[j], preNFC);
						flag = true;
						break;
					}
				}
				if (flag) {
					break;
				}
			}

		}
		return solu;
	}

	public static LinkedHashMap<NFtype, Node> updatePreNFC(int numPreSFs, Topology topo, Solution solu, Demand dem, int[] path) {
		int numSFC = dem.SFCCount();
		int numMaxSFs = getIdleSFsNum(topo, solu, path);
		LinkedHashMap<NFtype, Node> preNFC = new LinkedHashMap<NFtype, Node>(numSFC);
		// 空闲preSFs数量不满足
		if (numPreSFs > numMaxSFs) {
			return preNFC;
		}
		// 生成空闲VM中的预选preSFs集
		ArrayList<ArrayList<NFtype>> preSFsList = getIdleSFsList(numPreSFs, dem);
		int k = 0;
		do {
			//
			preNFC.clear();
			ArrayList<NFtype> preSFs = preSFsList.get(k);
			k++;
			int index = 1;
			for (int i = 0; i < path.length; i++) {
				//节点是否被禁用
				if (topo.getNode(path[i]).getBanFlag()) {
					continue;
				}

				// 注意vm为空
				VM vm = solu.getVM(path[i]);
				if (!vm.hasVM()) {
					// min(numPreSFs, SFmaxSize)
					for (int j = 0; j < numPreSFs && j < vm.getSFmaxSize(); j++) {
						NFtype nf = dem.getNFInSFC(index);
						if (openSFInVM(nf, vm, preSFs, preNFC)) {
							index++;
							if (index > numSFC) {
								return preNFC;
							}
						} else {
							// 没有检测到NF，则跳到下一个节点。
							break;
						}
					}
				} else {
					// 在一个节点的VM中最多需要找numSFC次
					for (int j = 1; j <= numSFC; j++) {
						NFtype nf = dem.getNFInSFC(index);
						if (checkSFInVM(dem, nf, vm, preNFC)) {
							index++;
							if (index > numSFC) {
								return preNFC;
							}
						} else {
							// 没有检测到NF，则跳到下一个节点。检测到的话接着在该节点检测下一个NF
							break;
						}
					}
					// 如果VM还有空闲的SF待安装
					int restedNum = vm.getSFmaxSize() - vm.getVMsum();
					if (restedNum > 0) {
						for (int j = 0; j < numPreSFs && j < restedNum; j++) {
							NFtype nf = dem.getNFInSFC(index);
							if (openSFInVM(nf, vm, preSFs, preNFC)) {
								index++;
								if (index > numSFC) {
									return preNFC;
								}
							} else {
								// 没有检测到NF，则跳到下一个节点。
								break;
							}
						}
					}
				}
			}
		} while (k < preSFsList.size());
		return preNFC;
	}

	public static void banVMs(Topology topo, int[] nodeid) {
		for (int i = 0; i < nodeid.length; i++) {
			topo.getNode(nodeid[i]).setBanFlag(true);
		}
	}

	public static void removeVM(Topology topo, Demands dems, Solution solu, int[] nodeid) {
		for (int i = 0; i < nodeid.length; i++) {
			VM revVM = solu.getVM(nodeid[i]);
			// 卸载以安装的请求流
			for (SFentry sfvm : revVM.getSFs()) {
				for (Flow flow : sfvm.getSFflows()) {
					dems.getDemand(flow.getId()).setIsInstall(false);
					solu.removeFlow(flow);
				}
			}
			// 关闭VM
			revVM.closeVM();
		}

	}

	/*
	 * 只能在没有被搬掉的节点检查
	 */
	public static void updateNFC(Solution solu, Topology topo, Demands demands) {
		for (Demand dem : demands.getDemands()) {
			// 该请求流被卸载
			if (!dem.getIsInstall()) {
				// 路径集根据最短优先级已排序
				int[][] paths = AllpathSolver.getAllpath(topo, dem.getSrcNoed(), dem.getDestNode(), demands.getMaxDelayRate());

				boolean flag = false;
				for (int j = 0; j < paths.length; j++) {
					// 生成指定路径上的预安装NFC
					LinkedHashMap<NFtype, Node> preNFC = updatePreNFC(0, topo, solu, dem, paths[j]);
					// TODO:判断该路径是否可以安装NFC
					if (preNFC.size() == dem.SFCCount()) {
						// TODO:可以安装
						createFlow(topo, solu, dem, paths[j], preNFC);
						flag = true;
						break;
					}
				}
				if (flag) {
					break;
				}
			}
		}

	}

	public static int getIdleSFsNum(Topology topo, Solution solu, int[] path) {
		int num = 0;
		for (int i = 0; i < path.length; i++) {

			VM vm = solu.getVM(i);
			if(topo.getNode(path[i]).getBanFlag()){
				continue;
			}
			if (!vm.hasVM() ) {
				num += vm.getSFmaxSize();
			} else if (vm.getVMsum() < vm.getSFmaxSize()) {
				num += vm.getSFmaxSize() - vm.getVMsum();
			}
		}
		return num;

	}

	/*
	 * public static LinkedHashMap<NFtype, Node> createPreNFC(Solution solu,
	 * Demand dem, int[] path){ int numSF = dem.SFCCount();
	 * LinkedHashMap<NFtype, Node> preNFC = new LinkedHashMap<NFtype,
	 * Node>(numSF); int index = 1; for(int i=0; i<path.length; i++){ //注意vm为空
	 * VM vm = solu.getVM(path[i]); if(vm==null || !vm.hasVM()){ continue; }
	 *
	 * //在一个节点的VM中最多需要找numSF次 for(int j=1; j<=numSF; j++){ NFtype nf =
	 * dem.getNFInSFC(index); if (checkSFInVM(dem, nf, vm, preNFC)) { index++;
	 * if (index > numSF) { return preNFC; } }else { break; } } } //没有被覆盖的情况
	 * //preNFC.clear(); return preNFC; }
	 */

	private static boolean checkSFInVM(Demand dem, NFtype nf, VM vm, LinkedHashMap<NFtype, Node> preNFC) {
		for (SFentry sf : vm.getSFs()) {
			if (sf.getSFname().equals(nf)) {
				// 检查各节点的NF容量
				if (dem.getSupply() + sf.getSFthroughput() <= sf.getSFmaxCapacity()) {
					// 添加到预备流中
					preNFC.put(sf.getSFname(), vm.getNode());
					return true;
				}
			}
		}
		return false;
	}

	private static boolean openSFInVM(NFtype nf, VM vm, ArrayList<NFtype> preSFs, LinkedHashMap<NFtype, Node> preNFC) {
		for (NFtype preSF : preSFs) {
			if (nf.equals(preSF)) {
				preNFC.put(nf, vm.getNode());
				return true;
			}
		}
		return false;
	}

	/**
	 * 新建流，更新VM信息
	 *
	 * @param topo
	 * @param solu
	 * @param dem
	 * @param path
	 */
	private static void createFlow(Topology topo, Solution solu, Demand dem, int[] path,
			LinkedHashMap<NFtype, Node> preNFC) {
		// 检查预先NFC链的合法性
		if (preNFC.size() != dem.SFCCount()) {
			throw new IllegalStateException(" createFlow preNFC is error! demand id: " + dem.getId());
		}
		// 新建流，更新流的需求信息
		solu.newFlow(dem.getId(), dem.getSrcNoed(), dem.getDestNode(), dem.getSupply(), dem.getShortedpathLength());
		// 更新流的路径信息,检查路径的合法性
		checkArgument(path.length >= 2, "flow_paths.length must >=2.", path.length);
		for (int i = 0; i < path.length - 1; i++) {
			Link link = topo.getLink(path[i], path[i + 1]);
			if (link == null) {

			}
			solu.addFlow(dem.getId(), link);
		}
		// 更新流的NFC信息,新建节点VM的各个NFs
		for (Entry<NFtype, Node> entry : preNFC.entrySet()) {
			solu.addFlow(dem.getId(), entry.getKey(), entry.getValue(), dem.getOrdinalInSFC(entry.getKey()));
			solu.addVM(entry.getValue(), entry.getKey(), solu.getFlow(dem.getId()));
		}
		// 清理preSFC
		// preNFC.clear();
		dem.setIsInstall(true);
	}

	/**
	 * 创建VM
	 *
	 * @param topo
	 * @param solu
	 */
	private static void createVM(Topology topo, Solution solu) {
		for (Node node : topo.getNodes()) {
			solu.newVM(node);
		}
	}

	/**
	 * 组合算法得到预选NF集，用于空闲VM中，C(k,n)中的k
	 *
	 * @param numVM
	 *            k
	 * @param numSF
	 *            n
	 * @return
	 */
	public static ArrayList<ArrayList<NFtype>> getIdleSFsList(int numVM, Demand dem) {
		int numSF = dem.SFCCount();
		if (numVM > numSF) {
			throw new IllegalStateException(" getIdleSFsInVM is error! numVM/numSF: " + numVM + "/" + numSF);
		}
		ArrayList<NFtype> data = new ArrayList<NFtype>(numSF);
		for (int i = 1; i <= numSF; i++) {
			data.add(dem.getNFInSFC(i));
		}
		ArrayList<ArrayList<NFtype>> preSFsList = new ArrayList<ArrayList<NFtype>>();
		getCombinerSelect(data, new ArrayList<NFtype>(), numSF, numVM, preSFsList);
		// System.out.println(preSFsList);
		return preSFsList;

	}

	/**
	 * 组合算法
	 *
	 * @param data
	 * @param workSpace
	 * @param n
	 * @param k
	 * @param preSFsInVM
	 */
	public static void getCombinerSelect(ArrayList<NFtype> data, ArrayList<NFtype> workSpace, int n, int k,
			ArrayList<ArrayList<NFtype>> preSFsInVM) {
		ArrayList<NFtype> copyData;
		ArrayList<NFtype> copyWorkSpace;

		if (workSpace.size() == k) {
			preSFsInVM.add(workSpace);
		}

		for (int i = 0; i < data.size(); i++) {
			copyData = new ArrayList<NFtype>(data);
			copyWorkSpace = new ArrayList<NFtype>(workSpace);

			copyWorkSpace.add(copyData.get(i));
			for (int j = i; j >= 0; j--)
				copyData.remove(j);
			getCombinerSelect(copyData, copyWorkSpace, n, k, preSFsInVM);
		}

	}

	/**
	 * 阶乘
	 *
	 * @param n
	 * @return
	 */
	public static int getFactorial(int n) {
		if (n == 0) {
			return 1;
		}
		return n * getFactorial(n - 1);
	}
}
