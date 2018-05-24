package com.zj.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;


import static com.google.common.base.Preconditions.*;


public class Topology {
	/*
	 * 拓扑名称
	 */
	private String _name;
	/*
	 * 节点集合
	 */
	private LinkedHashMap<Integer, Node> _nodes;
	/*
	 * 链路集合
	 */
	private LinkedHashMap<Integer, Link> _links;


	public Topology() {
		this("Topo-noName");
	}

	public Topology(String _name) {
		setName(_name);
		_nodes = new LinkedHashMap<Integer, Node>();
		_links = new LinkedHashMap<Integer, Link>();
	}


	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		checkNotNull(_name, "topology_name not is null.");
		this._name = _name;
	}


	/**
	 * 得到节点集合
	 * @return
	 */
	public Collection<Node> getNodes() {
		return _nodes.values();
	}

	/**
	 * 得到链路集合
	 * @return
	 */
	public Collection<Link> getLinks() {
		return _links.values();
	}

	/**
	 * 节点数量
	 * @return
	 */
	public int nodesCount(){
		return _nodes.size();
	}

	/**
	 * 链路数量
	 * @return
	 */
	public int linksCount(){
		return _links.size();
	}

	/**
	 * 检查节点集合中是否已有该节点
	 * @param node 节点
	 * @return
	 */
	public boolean containsNode(Node node){
		checkNotNull(node, "node is not null.");
		return node.equals(_nodes.get(node.getId()));
	}

	/**
	 * 检查链路集合中是否已有该链路
	 * @param link 链路
	 * @return
	 */
	/*此方法链路id出错
	public boolean containsLink(Link link){
		checkNotNull(link, "link is not null.");
		return link.equals(_links.get(link.getId()));
	}
	*/

	/**
	 * 检查链路集合中是否已有该链路
	 * @param fromnode 起始节点
	 * @param tonode 终止节点
	 * @return
	 */

	public boolean containsLink(Node fromnode, Node tonode){
		checkNotNull(fromnode, "fromnode is null.");
		checkNotNull(tonode, "tonode is null.");
		for(Link link : _links.values()){
			if(link.getFromNode().equals(fromnode) && link.getToNode().equals(tonode)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 找到节点集合中相应的节点
	 * @param nodeid 节点id
	 * @return
	 */
	public Node getNode(int nodeid){
		checkArgument(nodeid >= 0, "nodeid must >= 0.", nodeid);
		return _nodes.get(nodeid);
	}

	/**
	 * 找到链路集合中相应的链路
	 * @param linkid 链路id
	 * @return
	 */
	public Link getLink(int linkid){
		checkArgument(linkid > 0, "linkid Must >0", linkid);
		return _links.get(linkid);
	}

	/**
	 * 找到链路集合中相应的链路
	 * @param fromnode 起始节点
	 * @param tonode 终止节点
	 * @return
	 */
	public Link getLink(Node fromnode, Node tonode){
		checkNotNull(fromnode, "fromnode is null.");
		checkNotNull(tonode, "tonode is null.");
		for(Link link : _links.values()){
			if(link.getFromNode().equals(fromnode) && link.getToNode().equals(tonode)){
				return link;
			}
			// 检查流的路径是否与拓扑的链路反向
			if(link.getFromNode().equals(tonode) && link.getToNode().equals(fromnode)){
				return link;
			}
		}
		return null;
	}

	/**
	 * 找到链路集合中相应的链路
	 * @param fromid 起始节点id
	 * @param toid 终止节点id
	 * @return
	 */
	public Link getLink(int fromid, int toid){
		Node fromnode = getNode(fromid);
		Node tonode = getNode(toid);
		return getLink(fromnode, tonode);
	}

	/**
	 * 新建节点并添加到节点集合中
	 * @param nodeid 节点id ，默认节点内存
	 * @return
	 */
	public Node newNode(int nodeid){
		//checkArgument(nodeid >= 0, "nodeid must >= 0.", nodeid);
		Node node = new Node(nodeid);
		if(containsNode(node)){
			throw new IllegalStateException(
	                "this topology already contains a node with id " + nodeid);
		}
		_nodes.put(nodeid, node);
		return node;
	}

	/**
	 * 新建节点并添加到节点集合中
	 * @param nodeid 节点id
	 * @param weight 节点权重
	 * @return
	 */
	public Node newNode(int nodeid, float weight){
		//checkArgument(nodeid >= 0, "nodeid must >= 0.", nodeid);
		//checkArgument(weight >0, "node weight > 0.",memory);
		if(_nodes.containsKey(nodeid)){
			throw new IllegalStateException(
	                "this topology already contains a node with id " + nodeid);
		}
		Node node = new Node(nodeid, weight);
		_nodes.put(nodeid, node);
		return node;
	}

	/**
	 * 新建链路并添加到链路集合中
	 * @param fromnode 起始节点
	 * @param tonode 终止节点
	 * @param capacity 链路容量
	 * @return
	 */
	public Link newLink(Node fromnode, Node tonode, int capacity){
		//checkNotNull(fromnode, "link_fromNode is not null.");
		//checkNotNull(tonode, "link_toNode is not null.");
		//checkArgument(capacity >= 0, "link capacity must >= 0.", capacity);

		if(containsLink(fromnode,tonode)){
			throw new IllegalStateException(
	                "this topology already contains a link with fromnode/tonode "
	                + fromnode + "/" + tonode);
		}

		//检查反向链路，如果不需要添加
		if (containsLink(tonode, fromnode)) {
			return null;
		}
		Link link = new Link(fromnode, tonode, capacity);
		_links.put(link.getId(), link);

		return link;
	}

	/**
	 * 新建链路并添加到链路集合中
	 * @param fromid 起始节点id
	 * @param toid 终止节点id
	 * @param capacity 链路容量
	 * @return
	 */
	public Link newLink(int fromid, int toid, int capacity){
		Node fromnode = getNode(fromid);
		Node tonode = getNode(toid);
		return newLink(fromnode, tonode, capacity);
	}

	/**
	 * 删除链路
	 * @param link 链路
	 * @return
	 */
	public boolean removeLink(Link link){
		checkNotNull(link,"link is not null");
		if(!containsLink(link.getFromNode(), link.getToNode())){
			return false;
		}
		_links.remove(link.getId());
		return true;
	}

	/**
	 * 删除节点，并删除相邻链路
	 * @param node
	 * @return
	 */
	public boolean removeNode(Node node){
		checkNotNull(node, "node is not null.");
		if(!containsNode(node)){
			return false;
		}

		List<Link> linksToRemove = new LinkedList<Link>();
        for(Link link : _links.values()) {
            if(link.getFromNode().equals(node) || link.getToNode().equals(node)) {
                linksToRemove.add(link);
            }
        }
        for(Link link : linksToRemove) {
            removeLink(link);
        }
        _nodes.remove(node.getId());
        return true;
	}

	/**
	 * 得到节点在拓扑中的邻接节点集合
	 * @param node
	 * @return
	 */
	public ArrayList<Node> getAdjNodes(Node node){
		checkNotNull(node, "node is not null.");
		if (!containsNode(node)) {
			throw new IllegalStateException(
	                "this topology has not the node with id " + node.getId());
		}
		ArrayList<Node> adjNodes = new ArrayList<Node>();
		for(Link link : _links.values()){
			if(link.getFromNode().equals(node)) {
				adjNodes.add(link.getToNode());
	        }
			if (link.getToNode().equals(node)) {
				adjNodes.add(link.getFromNode());
			}
		}
		return adjNodes;
	}
}
