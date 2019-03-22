package com.zj.network;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.zj.util.ShortedpathSolver;

public class Demand implements Comparable<Demand> {

	/*
	 * 需求id(从1开始）
	 */
	private int _id;
	/*
	 * 源点
	 */
	private Node _srcNoed;
	/*
	 * 终点
	 */
	private Node _destNode;
	/*
	 * 流量
	 */
	private float _supply;
	/*
	 * 需要的服务功能链(次序，NF)，即NFC
	 */
	private LinkedHashMap<Integer, NFtype> _SFC;
	/*
	 * 最短路径长度
	 */
	private int _shortedpathLength;
	/*
	 * 是否被安装
	 */
	private boolean _isInstall;

	public Demand(int _id, Node _srcNoed, Node _destNode, float _supply, int[] _SFC, Topology topology) {
		//super();
		setId(_id);
		setSrcNoed(_srcNoed);
		setDestNode(_destNode);
		setSupply(_supply);
		setSFC(_SFC);
		setShortedpathLength(ShortedpathSolver.getPathLength(topology, _srcNoed, _destNode, true));
		setIsInstall(false);
	}
	public int getId() {
		return _id;
	}
	public void setId(int _id) {
		checkArgument(_id >= 1, "node_id must >= 1.", _id);
		this._id = _id;
	}
	public Node getSrcNoed() {
		return _srcNoed;
	}
	public void setSrcNoed(Node _srcNoed) {
		checkNotNull(_srcNoed, "demand_srcNode is not null.");
		this._srcNoed = _srcNoed;
	}
	public Node getDestNode() {
		return _destNode;
	}
	public void setDestNode(Node _destNode) {
		checkNotNull(_destNode, "demand_destNode is not null.");
		this._destNode = _destNode;
	}
	public float getSupply() {
		return _supply;
	}
	public void setSupply(float _supply) {
		checkArgument(_supply >= 0, "demand_supply must >= 0.", _supply);
		this._supply = _supply;
	}


	public boolean getIsInstall() {
		return _isInstall;
	}
	public void setIsInstall(boolean _isInstall) {
		this._isInstall = _isInstall;
	}
	/**
	 * 得到服务功能链(SFC)的NF数量
	 * @return
	 */
	public int SFCCount(){
		return _SFC.size();
	}

	/**
	 * 通过序号查找在服务功能链(SFC)的NF
	 * @param ordinal
	 * @return
	 */
	public NFtype getNFInSFC(int ordinal){
		NFtype nf = _SFC.get(ordinal);
		checkNotNull(nf, "demand_NF is not null.");
		return nf;
	}

	/**
	 * 得到NF在服务功能链(SFC)的序号
	 * @param nf
	 * @return
	 */
	public int getOrdinalInSFC(NFtype nf){
		checkNotNull(nf, "demand_NF is not null.");
		for(Entry<Integer, NFtype> entry : _SFC.entrySet()){
			if (nf.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		throw new IllegalStateException(
                "this demand_SFC does not contains a NF. " + nf);

	}

	public void setSFC(int[] array) {
		_SFC = new LinkedHashMap<Integer, NFtype>();
		for (int ordinal = 1; ordinal <= array.length; ordinal++) {
			for (int i = 0; i < array.length; i++) {
				if(array[i] == ordinal){
					_SFC.put(ordinal, NFtype.getNFtypeByIndex(i));
				}
			}
		}
	}
	public int getShortedpathLength() {
		return _shortedpathLength;
	}
	public void setShortedpathLength(int _shortedpathLength) {
		this._shortedpathLength = _shortedpathLength;
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Demand)){
			return false;
		}
		Demand demand = (Demand) obj;
		return demand.getId() == _id;
	}
	@Override
	public String toString() {
		return "Demand [_id=" + _id + ", _srcNoed=" + _srcNoed + ", _destNode=" + _destNode + ", _supply=" + _supply
				+ ", _SFC=" + _SFC + "]";
	}
	@Override
	public int compareTo(Demand dem) {
		if (this.SFCCount() < dem.SFCCount() ) {
			return -1;
		}else if (this.SFCCount() == dem.SFCCount()) {
			float a = this.getNFInSFC(1).getNFcapacity();
			float b = dem.getNFInSFC(1).getNFcapacity();
			if (a > b) {
				return -1;
			}else if (a < b) {
				return 1;
			}else {
				return 0;
			}
		}else {
			return 1;
		}
	}


}
