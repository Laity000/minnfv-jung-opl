package com.zj.network;


import static com.google.common.base.Preconditions.*;

import java.util.LinkedHashMap;

public class Node {
	/*
	 * 默认最大内存
	 */
	static final float DEFAULT_WEIGHT = (float) 1.0;
	/*
	 * 节点id
	 */
	private int _id;
	/*
	 * 节点的权重
	 */
	private float _weight;
	/*
	 * 节点禁用标志
	 */
	private boolean _isBan = false;

	//private LinkedHashMap<NFtype, Integer> _VM;

	public Node(int _id, float _weight) {

		setId(_id);
		setWeight(_weight);
		//_VM = new LinkedHashMap<NFtype, Integer>();
	}

	public Node(int _id) {

		setId(_id);
		setWeight(DEFAULT_WEIGHT);
		//_VM = new LinkedHashMap<NFtype, Integer>();
	}

	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		checkArgument(_id >= 0, "node_id must >= 0.", _id);
		this._id = _id;
	}

	public float getWeight() {
		return _weight;
	}

	public void setWeight(float _weight) {
		checkArgument(_weight > 0, "node_weight must > 0.", _weight);
		this._weight = _weight;
	}



	public boolean getBanFlag() {
		return _isBan;
	}

	public void setBanFlag(boolean _isBan) {
		this._isBan = _isBan;
	}

	/*
	public boolean hasVM(){
		return (_VM != null && !_VM.isEmpty());
	}

	public boolean hasNF(NFtype nf){
		return _VM.containsKey(nf);
	}


	public int getNFtroughput(NFtype nf){
		if(!hasVM()){
			System.err.println("node" + _id + "has not VM");
			return 0;
		}
		if(!hasNF(nf)){
			System.err.println("node" + _id + "has not NF:" + nf);
			return 0;
		}
		return _VM.get(nf);
	}

	public String getVMinfo(){
		String info = "";
		if(!hasVM()){
			info = "N" + _id + " has not VM! ";
		}else{
			for(NFtype nf : _VM.keySet()){
				info += nf.toString() + ":" + _VM.get(nf) + "Mbp  ";
			}
		}
		return info;
	}


	public void addNF(NFtype nf, int throughput){
		if (_VM.containsKey(nf)) {
			int newThroughput = _VM.get(nf) + throughput;
			_VM.put(nf, newThroughput);
		}else {
			_VM.put(nf, throughput);
		}
	}
    */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Node)){
			return false;
		}
		Node node = (Node) obj;
		return node.getId() == _id;
	}

	@Override
	public String toString() {
		//return "Node [_id=" + _id + ", _memory=" + _memory + ", _VM=" + getVMinfo() + "]";
		return _id + "";
	}
}
