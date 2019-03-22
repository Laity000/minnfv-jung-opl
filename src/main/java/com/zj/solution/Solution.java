package com.zj.solution;

import static com.google.common.base.Preconditions.*;

import java.util.Collection;
import java.util.LinkedHashMap;

import com.zj.network.Link;
import com.zj.network.NFtype;
import com.zj.network.Node;
import com.zj.solution.Flow.NFentry;

public class Solution implements Comparable<Solution>{
	/*
	 * 解决方案名称
	 */
	private String _name;
	/*
	 * 需求数量
	 */
	private int _numDems;
	/*
	 * 流集合
	 */
	private LinkedHashMap<Integer, Flow> _flows;
	/*
	 * VM
	 */
	private LinkedHashMap<Integer, VM> _VMs;

	public Solution(int numDems) {
		this("Solu-noName", numDems);
	}

	public Solution(String _name, int numDems) {
		setName(_name);
		setNumDems(numDems);
		_flows = new LinkedHashMap<Integer, Flow>();
		_VMs = new LinkedHashMap<Integer, VM>();
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		checkNotNull(_name, "solution_name not is null.");
		this._name = _name;
	}

	public int getNumDems() {
		return _numDems;
	}

	public void setNumDems(int _numDems) {
		checkArgument(_numDems >= 0, "numDems must >= 0.", _numDems);
		this._numDems = _numDems;
	}

	/**
	 * 得到流集合
	 * @return
	 */
	public Collection<Flow> getFlows(){
		return _flows.values();
	}
	/**
	 * 得到流id集合
	 * @return
	 */
	public Collection<Integer>getFlowIdList(){
		return _flows.keySet();
	}
	/**
	 * 得到流
	 * @param flowid 流id
	 * @return
	 */
	public Flow getFlow(int flowid){
		return _flows.get(flowid);
	}
	/**
	 * 流集合数量
	 * @return
	 */
	public int flowsCount(){
		return _flows.size();
	}

	/**
	 * 检查流集合中是否已有该流
	 * @param flow 流
	 * @return
	 */
	public boolean containsFlow(Flow flow){
		checkNotNull(flow, "flow is not null.");
		return flow.equals(_flows.get(flow.getId()));
	}

	/**
	 * 检查流集合中是否已有该流
	 * @param flowid 流id
	 * @return
	 */
	public boolean containsFlow(int flowid){
		checkArgument(flowid > 0, "flow_id must > 0.", flowid);
		return getFlow(flowid) != null;
	}

	/**
	 * 新建流
	 * @param flowid
	 * @param srcNode
	 * @param destNode
	 * @return
	 */
	public Flow newFlow(int flowid, Node srcNode, Node destNode, float supply, int shortpathLength){
		Flow flow = new Flow(flowid, srcNode, destNode, supply, shortpathLength);
		_flows.put(flowid, flow);
		return flow;
	}

	/**
	 * 添加流到流集合中
	 * @param flow 流
	 * @return
	 * @return
	 */
	public boolean addFlow(Flow flow){
		checkNotNull(flow, "flow is not null.");
		if(containsFlow(flow)){
			throw new IllegalStateException(
	                "this solution already contains a flow with id " + flow.getId());
			//return false;
		}
		else{
			_flows.put(flow.getId(), flow);
			return true;
		}
	}

	/**
	 * 添加/修改流到流集合中（链路）
	 * @param flowid 链路
	 * @param path
	 * @return
	 */
	public boolean addFlow(int flowid, Link link){
		//checkArgument(flowid > 0, "nodeid must > 0.", flowid);
		//checkNotNull(link, "link is not null.");
		if(containsFlow(flowid)){
			//
			getFlow(flowid).addPath(link);
			return false;
		}else{
			Flow flow = new Flow(flowid);
			flow.addPath(link);
			_flows.put(flowid, flow);
			return true;
		}
	}

	/**
	 * 添加/修改流到流集合中（路径）
	 * @param flowid 流id
	 * @param paths 流路径集
	 * @return
	 */
	public boolean addFlow(int flowid, Collection<Link> paths){
		//checkArgument(flowid > 0, "nodeid must > 0.", flowid);
		//checkNotNull(paths, "path is not null.");
		if(containsFlow(flowid)){
			//
			getFlow(flowid).addPath(paths);
			return false;
		}else{
			Flow flow = new Flow(flowid);
			flow.addPath(paths);
			_flows.put(flowid, flow);
			return true;
		}
	}

	/**
	 * 添加/修改流到流集合中（NFC）
	 * @param flowid
	 * @param nfc
	 * @return
	 */
	public boolean addFlow(int flowid, NFtype NFname, Node NFnode, int NForder){
		//checkArgument(flowid > 0, "nodeid must > 0.", flowid);
		if(containsFlow(flowid)){
			//
			getFlow(flowid).addNFC(NFname, NFnode, NForder);
			return false;
		}else{
			Flow flow = new Flow(flowid);
			flow.addNFC(NFname, NFnode, NForder);
			_flows.put(flowid, flow);
			return true;
		}
	}

	/**
	 * 添加/修改流到流集合中（路径、NFC）
	 * @param flowid
	 * @param paths
	 * @param nfc
	 * @return
	 */
	public boolean addFlow(int flowid, Collection<Link> paths, NFtype NFname, Node NFnode, int NForder){
		//checkArgument(flowid > 0, "nodeid must > 0.", flowid);
		//checkNotNull(paths, "path is not null.");
		if(containsFlow(flowid)){
			getFlow(flowid).addNFC(NFname, NFnode, NForder);
			return false;
		}else{
			Flow flow = new Flow(flowid);
			flow.addPath(paths);
			flow.addNFC(NFname, NFnode, NForder);
			_flows.put(flowid, flow);
			return true;
		}

	}

	/**
	 * 添加流到流集合中
	 * @param flowid 流id
	 * @param link 流路径数组
	 * @return
	 */
	/*
	public Flow addFlow(int flowid, Link... link){
		checkArgument(flowid > 0, "nodeid must > 0.", flowid);
		checkNotNull(link, "path is not null.");
		Flow flow = new Flow(flowid);
		if(containsFlow(flow)){
			throw new IllegalStateException(
	                "this solution already contains a flow with id " + flowid);
		}
		for (int i = 0; i < link.length; i++) {
			flow.addPath(link[i]);
		}
		_flows.put(flowid, flow);
		return flow;

	}
	*/
	/**
	 * 删除流
	 * @param flow 流
	 * @return
	 */
	public boolean removeFlow(Flow flow){
		checkNotNull(flow,"flow is not null");
		if(!containsFlow(flow)){
			return false;
		}
		_flows.remove(flow.getId());
		return true;
	}

	/**
	 * 得到节点的VM
	 * @param nodeid
	 * @return
	 */
	public VM getVM(int nodeid){
		return _VMs.get(nodeid);
	}

	/**
	 * 得到VMs集合
	 * @return
	 */
	public Collection<VM> getVMs() {
		return _VMs.values();
	}

	/**
	 * 新建VM
	 * @param node
	 */
	public void newVM(Node node){
		checkNotNull(node, "this solution_vm has not the node! ");
		VM vm =new VM(node);
		_VMs.put(node.getId(), vm);
	}

	/**
	 * 添加每个流的NFC到VM的NFs集合
	 * @param node 节点VM
	 * @param nf NF
	 * @param flow 流
	 */
	public void addVM(Node node, NFtype nf, Flow flow){
		checkNotNull(node, "this solution_vm has not the node! ");
		checkNotNull(flow, "this solution_vm has not the flow! ");
		VM vm;
		if(_VMs.containsKey(node.getId())){
			vm = _VMs.get(node.getId());
		}else {
			vm = new VM(node);
		}
		vm.addNF(nf, flow);
		_VMs.put(node.getId(), vm);
	}

	public float getAvgSPLen() {
		float sumSPLen = 0;
		int sum = 0;
		for (Flow flow : getFlows()) {
			sumSPLen += flow.getShortedpathLength();
		}
		return sumSPLen/flowsCount();
	}


	public float getAvgCPLen() {
		float sumCPLen = 0;
		for (Flow flow : getFlows()) {
			sumCPLen += flow.getCurrentpathLength();
		}
		return sumCPLen/flowsCount();
	}

	public int getActiveEnergy() {
		int energy = 0;
		for (VM vm : getVMs()) {
			energy +=vm.getVMEnergy();
		}
		return energy;
	}

	public double getFitValue(){
		if (getFlows().size() != getNumDems()) {
			return Float.MAX_VALUE;
		}else {
			return getActiveEnergy() + 0.1 * getAvgCPLen();
		}

	}

	public int compareTo(Solution o) {
		if (this.getFitValue() > o.getFitValue()) {
			return 1;
			}else if (this.getFitValue() < o.getFitValue()) {
				return -1;
			}else {
				return 0;
		}

	}

}
