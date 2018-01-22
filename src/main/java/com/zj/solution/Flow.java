package com.zj.solution;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.zj.network.Link;
import com.zj.network.NFtype;
import com.zj.network.Node;

import ilog.cplex.cppimpl.intArray;

public class Flow {
	/*
	 * 流的id
	 */
	private int _id;
	/*
	 * 流的源点，对应需求的源点
	 */
	private Node _srcNode;
	/*
	 * 流的终点，对应需求的终点
	 */
	private Node _destNode;
	/*
	 * 最短路径长度
	 */
	private int _shortedpathLength;
	
	/*
	 * 流的流量
	 */
	private float _supply;
	/*
	 * 流的路径集合
	 */
	private ArrayList<Link> _path;
	/*
	 * 流的NFs链，序号指明次序，内容指明安的NF
	 */
	private ArrayList<NFentry> _NFC;

	/*
	 * 网络功能NF
	 */
	public class NFentry {
		//NF的名称
		private NFtype _NFname;
		//NF被安放的节点
		private Node _NFnode;
		//NF在NFC中的次序
		private int _NFordinal;

		public NFtype getNFname() {
			return _NFname;
		}

		public void setNFname(NFtype _NFname) {
			this._NFname = _NFname;
		}

		public Node getNFnode() {
			return _NFnode;
		}

		public void setNFnode(Node _NFnode) {
			checkNotNull(_NFnode, "flow_NFnode is not null.");
			this._NFnode = _NFnode;
		}

		public int getNFordinal() {
			return _NFordinal;
		}

		public void setNFordinal(int _NFordinal) {
			checkArgument(_NFordinal >= 0, "flow_NFordinal must >= 0.", _id);
			this._NFordinal = _NFordinal;
		}

		public NFentry(NFtype _NFname, Node _NFnode, int _NForder) {
			//super();
			setNFname(_NFname);
			setNFnode(_NFnode);
			setNFordinal(_NForder);
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof NFentry)){
				return false;
			}
			NFentry nf = (NFentry) obj;
			return nf.getNFname().equals(_NFname);
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return " NF[_name=" + _NFname + ", node=" + _NFnode.getId()
			+ ", NFordinal=" + _NFordinal + "]";
		}

	}

	public Flow(int _id) {
		setId(_id);
		_path = new ArrayList<Link>();
		_NFC = new ArrayList<NFentry>();
	}


	public Flow(int _id, Node _srcNode, Node _destNode, float _supply, int _shortedpathLength) {
		this(_id);
		setSrcNode(_srcNode);
		setDestNode(_destNode);
		setSupply(_supply);
		setShortedpathLength(_shortedpathLength);
	}


	public int getId() {
		return _id;
	}
	public void setId(int _id) {
		checkArgument(_id > 0, "flow_id must > 0.", _id);
		this._id = _id;
	}
	public Node getSrcNode() {
		return _srcNode;
	}
	public void setSrcNode(Node _srcNode) {
		checkNotNull(_srcNode, "flow_srcNode is not null.");
		this._srcNode = _srcNode;
	}
	public Node getDestNode() {
		return _destNode;
	}
	public void setDestNode(Node _destNode) {
		checkNotNull(_destNode, "flow_destNode is not null.");
		this._destNode = _destNode;
	}


	public float getSupply() {
		return _supply;
	}



	public int getShortedpathLength() {
		return _shortedpathLength;
	}


	public void setShortedpathLength(int _shortedpathLength) {
		this._shortedpathLength = _shortedpathLength;
	}


	public int getCurrentpathLength() {
		return getPath().size();
	}

	public void setSupply(float _supply) {
		checkArgument(_supply > 0, "flow_supply must > 0.", _supply);
		this._supply = _supply;
	}


	public ArrayList<Link> getPath() {
		return _path;
	}

	public ArrayList<NFentry> getNFC() {
		return _NFC;
	}

	/**
	 * 检查路径是否存在
	 * @param link 路径/链路
	 * @return
	 */
	public boolean containsPath(Link link){
		for(int i=0; i<_path.size(); i++){
			if(_path.get(i).equals(link)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查NF是否在NFC中
	 * @param nf NF
	 * @return
	 */
	public boolean containsNF(NFentry nf){
		for(int i=0; i<_NFC.size(); i++){
			if(_NFC.get(i).equals(nf)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 添加链路到路径集合
	 * @param link 链路
	 */
	public void addPath(Link link){
		checkNotNull(link, "flow_link is not null.");
		if(containsPath(link)){
			throw new IllegalStateException(
		               "Maybe circuit. this paths already contains a link with id. " + link.getId());
		}
		_path.add(link);
	}

	/**
	 * 添加路径到路径集合
	 * @param path 路径/连接集
	 */
	public void addPath(Collection<Link> path){
		checkNotNull(path, "flow_path is not null.");
		for(Link link : path){
			addPath(link);
		}
	}

	/**
	 * 添加NF到NFC中
	 * @param nf
	 */
	public void addNFC(NFentry nf){
		checkNotNull(nf, "flow_nf is not null.");
		if(containsNF(nf)){
			throw new IllegalStateException(
		               "this NFC already contains a NF with name. " + nf.getNFname());
		}

		//nf.getNFnode().addNF(nf.getNFname(), 1);
		_NFC.add(nf);
	}

	/**
	 * 添加NF到NFC中
	 * @param NFname
	 * @param NFnode
	 * @param NFordinal
	 */
	public void addNFC(NFtype NFname, Node NFnode, int NFordinal){
		NFentry nf = new NFentry(NFname,NFnode,NFordinal);
		addNFC(nf);
	}

	/**
	 * 添加NFs到NFC中
	 * @param nfc
	 */
	public void addNFC(Collection<NFentry> nfc){
		checkNotNull(nfc, "flow_nfc is not null.");
		for(NFentry nf : nfc){
			addNFC(nf);
		}
	}

	public boolean validate(){
		//1、不能有环；2、源目的与需求相同；3、NFC与需求相等；4、NFC在流上
		if(_path == null && _path.isEmpty()){
			return false;
		}else {
			return true;
		}

	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Flow)){
			return false;
		}
		Flow flow = (Flow)obj;
		return flow.getId() == _id;
	}


	@Override
	public String toString() {
		String ss = ", _NFC={";
		for (NFentry nf : getNFC()){
			 ss += nf.toString();
		}
		/*
		return "Flow [_id=" + _id + ", _srcNode=" +
	_srcNode + ", _destNode=" + _destNode + ", _paths=" + _path + "]"  + ss + "}";
	*/
		return "Flow [_id=" + _id + ", _src/dest=" + _srcNode.getId() + "/" +_destNode.getId() +
				", _spLen=" + _shortedpathLength + ", cpLen=" + getPath().size() + "]" + ss + "}";
	}

}
