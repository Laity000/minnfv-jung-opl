package com.zj.data;


import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.zj.network.NFtype;

public class JsonBean {

	/*
	 * Bean
	 */
	private int numNodes;
	
	private ArrayList<Float> nodeWeight;

	private ArrayList<JsonArcs> arcs;

	private ArrayList<JsonFlow> flows;

	private ArrayList<JsonTempOrdinal> tempOrdinals;

	private ArrayList<JsonDemand> demands;

	public static class JsonArcs{

		private int fromNodeID;

		private int toNodeID;

		private int capacity;

		public int getFromNodeID() {
			return fromNodeID;
		}

		public int getToNodeID() {
			return toNodeID;
		}

		public int getCapacity() {
			return capacity;
		}

	}

	public static class JsonDemand{

		private int id;

		private int srcNodeID;

		private int destNodeID;

		private float supply;

		private int[] SFC;

		public int getId() {
			return id;
		}

		public int getSrcNodeID() {
			return srcNodeID;
		}

		public int getDestNodeID() {
			return destNodeID;
		}

		public float getSupply() {
			return supply;
		}

		public int[] getSFC() {
			return SFC;
		}

	}

	public static class JsonFlow{

		private int demandID;

		private int fromNodeID;

		private int toNodeID;

		public int getDemandID() {
			return demandID;
		}

		public int getFromNodeID() {
			return fromNodeID;
		}

		public int getToNodeID() {
			return toNodeID;
		}

	}

	public static class JsonTempOrdinal{
		private int demandID;

		private int nodeID;

		private String NFString;

		private int ordinal;

		public int getDemandID() {
			return demandID;
		}

		public int getNodeID() {
			return nodeID;
		}

		public String getNFString() {
			return NFString;
		}

		public int getOrdinal() {
			return ordinal;
		}

	}

	public int getNumNode() {
		return numNodes;
	}
	
	public ArrayList<Float> getNodeWeight() {
		return nodeWeight;
	}

	public void setNodeWeight(ArrayList<Float> nodeWeight) {
		this.nodeWeight = nodeWeight;
	}

	public ArrayList<JsonArcs> getArcs() {
		return arcs;
	}

	public ArrayList<JsonFlow> getFlows() {
		return flows;
	}

	public ArrayList<JsonTempOrdinal> getTempOrdinals() {
		return tempOrdinals;
	}

	public ArrayList<JsonDemand> getDemands() {
		return demands;
	}




}
