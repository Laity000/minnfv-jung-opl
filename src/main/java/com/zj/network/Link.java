package com.zj.network;

import static com.google.common.base.Preconditions.*;

public class Link {

	private static int count = 0;
	/*
	 * 链路id（自加）
	 */
	private int _id;
	/*
	 * 链路（无向图）的起始节点
	 */
	private Node _fromNode;
	/*
	 * 链路（无向图）的终止节点
	 */
	private Node _toNode;
	/*
	 * 链路的容量
	 */
	private int _capacity;

	public Link(Node fromNode, Node toNode, int capacity) {
		//super();

		count++;
		this._id = count;
		setFromNode(fromNode);
		setToNode(toNode);
		setCapacity(capacity);
	}

	public int getId() {
		return _id;
	}

	public Node getFromNode() {
		return _fromNode;
	}

	public void setFromNode(Node _fromNode) {
		checkNotNull(_fromNode, "link_fromNode is not null.");
		this._fromNode = _fromNode;
	}

	public Node getToNode() {
		return _toNode;
	}

	public void setToNode(Node _toNode) {
		checkNotNull(_toNode, "link_toNode is not null.");
		this._toNode = _toNode;
	}

	public int getCapacity() {
		return _capacity;
	}

	public void setCapacity(int _capacity) {
		checkArgument(_capacity >= 0, "link_capacity must >= 0.", _capacity);
		this._capacity = _capacity;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Link)){
			return false;
		}
		Link link = (Link) obj;
		return (link.getFromNode().equals(_fromNode) && link.getToNode().equals(_toNode));
	}

	@Override
	public String toString() {

		return "Link [_id=" + _id + ", _fromNode=" + _fromNode + ","
				+ " _toNode=" + _toNode  + "]";

		//return _capacity + "";
	}

}
