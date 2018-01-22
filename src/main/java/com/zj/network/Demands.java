package com.zj.network;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.LinkedHashMap;

public class Demands {

	public static final int DEFAULT_DELAYRATE = 2;

	/*
	 * 需求集合名称
	 */
	private String _name;

	/*
	 * 需求集合映射
	 */
	private LinkedHashMap<Integer, Demand> _demands;
	/*
	 * 最大时延比
	 */
	private int _maxDelayRate;



	public Demands(String _name) {
		this("Dems-noName", DEFAULT_DELAYRATE);
	}

	public Demands(String _name, int _maxDelay) {
		checkNotNull(_name, "demands_name not is null.");
		setMaxDelayRate(_maxDelay);
		this._name = _name;
		_demands = new LinkedHashMap<Integer, Demand>();
	}

	public String getName() {
		return _name;
	}



	public int getMaxDelayRate() {
		return _maxDelayRate;
	}

	public void setMaxDelayRate(int _maxDelayRate) {
		checkArgument(_maxDelayRate >= 1, "dems_maxDelayRate must >= 1.0!", _maxDelayRate);
		this._maxDelayRate = _maxDelayRate;
	}

	/**
	 * 得到需求集合
	 * @return
	 */
	public Collection<Demand> getDemands() {
		return _demands.values();
	}

	/**
	 * 需求数量
	 */
	public int demandsCount(){
		return _demands.size();
	}

	/**
	 * 检查需求集合中是否已有该需求
	 * @param demand 需求
	 * @return
	 */
	public boolean containsDemand(Demand demand){
		checkNotNull(demand, "demand is not null.");
		return demand.equals(_demands.get(demand.getId()));
	}

	/**
	 * 找到需求集合中相应的需求
	 * @param demandid 需求id
	 * @return
	 */
	public Demand getDemand(int demandid){
		checkArgument(demandid > 0, "nodeid must > 0.", demandid);
		return _demands.get(demandid);
	}

	public Demand newDemand(int demandid, Node srcNode, Node destNode, float supply, int[] SFC, Topology topology){
		Demand demand = new Demand(demandid, srcNode, destNode, supply, SFC, topology);
		if(containsDemand(demand)){
			throw new IllegalStateException(
	                "this demands already contains a demand with id " + demandid);
		}

		_demands.put(demandid, demand);
		return demand;

	}




}
