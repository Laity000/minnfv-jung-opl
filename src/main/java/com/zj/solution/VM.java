package com.zj.solution;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.zj.main.App;
import com.zj.network.NFtype;
import com.zj.network.Node;
import com.zj.solution.Flow.NFentry;

public class VM {

	static final int DEFAULT_SFSIZE = App.VM_CAPACITY;
	//static final float DEFAULT_SFCAPACITY =13;

	/*
	 * VM安放的节点
	 */
	private Node _node;

	/*
	 * VM的SF数量限制
	 */
	private int _SFmaxSize;

	/*
	 * VM激活标志
	 */
	private boolean _isVM = false;

	/*
	 * VM放置的SF集合
	 */
	private LinkedHashMap<NFtype, SFentry> _SFs;

	/*
	 * 服务功能SF
	 */
	public class SFentry{

		//SF名称
		private NFtype _SFname;
		//SF的吞吐容量限制
		private float _SFmaxCapacity;
		//SF的吞吐量
		private float _SFthroughput;
		//SF经过的流集合
		private ArrayList<Flow> _SFflows;


		public SFentry(NFtype _SFname, float _SFthroughput, Flow flow) {
			setSFname(_SFname);
			setSFthroughput(_SFthroughput);
			//setSFmaxCapacity(DEFAULT_SFCAPACITY);
			setSFmaxCapacity(_SFname.getNFcapacity());
			_SFflows = new ArrayList<Flow>();
			addSFflow(flow);

		}
		public NFtype getSFname() {
			return _SFname;
		}
		public void setSFname(NFtype _SFname) {
			this._SFname = _SFname;
		}
		public float getSFmaxCapacity() {
			return _SFmaxCapacity;
		}
		public void setSFmaxCapacity(float _SFmaxCapacity) {
			checkArgument(_SFmaxCapacity >= 0, "VM_SFmaxCapacity must >= 0.",
					_SFname.toString() + ":" + _SFmaxCapacity);
			this._SFmaxCapacity = _SFmaxCapacity;
		}
		public float getSFthroughput() {
			return _SFthroughput;
		}
		public void setSFthroughput(float _SFthroughput) {
			checkArgument(_SFthroughput >= 0, "VM_NFmaxCapacity must >= 0.",
					_SFname.toString() + ":" + _SFthroughput);
			this._SFthroughput = _SFthroughput;
		}
		public ArrayList<Flow> getSFflows() {
			return _SFflows;
		}


		public void addSFflow(Flow flow) {
			checkNotNull(flow, "VM_SFflow is not null. ", _SFname.toString());
			_SFflows.add(flow);
		}
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof NFentry)){
				return false;
			}
			SFentry sf = (SFentry) obj;
			return sf.getSFname().equals(_SFname);
		}
		@Override
		public String toString() {
			String info = "";
			for(Flow flow : getSFflows()){
				info += flow.getId() + ",";
			}
			return getSFname() + ": (" + getSFthroughput()/getSFmaxCapacity()
					+ "个)"+ getSFthroughput() + "Mpb, _F:{" + info + "}  ";
		}

	}


	public VM(Node _node) {
		setNode(_node);
		setSFmaxSize(DEFAULT_SFSIZE);
		_SFs = new LinkedHashMap<NFtype, VM.SFentry>();
	}


	/**
	 * 得到VM的SFs集合
	 * @return
	 */
	public Collection<SFentry> getSFs() {
		return _SFs.values();
	}

	public SFentry getSF(NFtype sf){
		return _SFs.get(sf);
	}

	/**
	 * 得到VM的SF数量限制
	 * @return
	 */
	public int getSFmaxSize() {
		return _SFmaxSize;
	}

	public void setSFmaxSize(int _NFmaxSize) {
		checkArgument(_NFmaxSize >= 0, "VM_SFmaxSize must >= 0.", _SFmaxSize);
		this._SFmaxSize = _NFmaxSize;
	}

	public void setNode(Node _node) {
		checkNotNull(_node, "VM_node is not null.");
		this._node = _node;
	}

	/**
	 * 得到VM所在的节点
	 * @return
	 */
	public Node getNode() {
		return _node;
	}



	/**
	 * VM是否激活
	 * @return
	 */
	public boolean hasVM(){
		return _isVM;
	}


	/**
	 * 关闭VM
	 */
	public void closeVM(){
		if(hasVM()){
			_isVM = false;
			getSFs().clear();
		}
	}

	/**
	 * VM中是否放置了该SF
	 * @param sf
	 * @return
	 */
	public boolean hasSF(NFtype sf){
		return _SFs.containsKey(sf);
	}

	/**
	 * 向VM中添加具体的NF
	 * @param nf
	 * @param flow
	 */
	public void addNF(NFtype sfname, Flow flow){
		_isVM = true;
		if(hasSF(sfname)){
			SFentry sf = getSF(sfname);
			float newThroughput = sf.getSFthroughput() + flow.getSupply();
			sf.setSFthroughput(newThroughput);
			sf.addSFflow(flow);
		}else {
			SFentry sf = new SFentry(sfname, flow.getSupply(), flow);
			_SFs.put(sfname, sf);
		}
	}

	/**
	 * 得到节点的VM总数
	 * @return
	 */
	public int getVMsum(){
		int sum = 0;
		for(SFentry sf : getSFs()){
			float num = sf.getSFthroughput()/sf.getSFmaxCapacity();
			sum += Math.ceil(num);
		}
		return sum;
	}

	public double getVMEnergy() {

		//double energy = getVMsum() *  273.5;
		double energy = getVMsum();
		if (energy != 0) {
			//energy += 80.5;
			energy += 10 * getNode().getWeight();
		}
		return energy;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof VM)){
			return false;
		}
		VM vm = (VM) obj;
		return vm.getNode().equals(_node);
	}

	@Override
	public String toString() {
		if (!hasVM()) {
			return "VM [_nodeid=" + _node.getId() + ", _SFs is null";
		}else {
			String ss = ", _SFs=";
			for(SFentry sf : getSFs()){
				ss += sf.toString();
			}
			return "VM [_nodeid=" + _node.getId() + " _sum=" + getVMsum() + ss + "]";
		}
	}
}
