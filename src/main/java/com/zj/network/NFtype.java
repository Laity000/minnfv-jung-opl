package com.zj.network;

public enum NFtype {
	NF1(0, "Firewall", 6), NF2(1, "IDS", 4), NF3(2, "Proxy", 3),
	NF4(3, "NAT", 8), NF5(4, "LB", 12), NF6(5, "FM", 0);
	//NF1(0, "Firewall", 5), NF2(1, "IDS", 5), NF3(2, "Proxy", 5),
	//NF4(3, "NAT", 5), NF5(4, "LB", 5), NF6(5, "FM", 5);

	private int _index;
	
	private String _name;

	private float _NFcapacity;

	private NFtype(int _index, String _name, int _NFcapacity) {
		this._index = _index;
		this._name = _name;
		this._NFcapacity = _NFcapacity;
	}

	public int getIndex() {
		return _index;
	}
	
	public String getName() {
		return _name;
	}
 

	public float getNFcapacity() {
		return _NFcapacity;
	}

	public static NFtype getNFtypeByIndex(int index){
		switch (index) {
		case 0:
			return NF1;
		case 1:
			return NF2;
		case 2:
			return NF3;
		case 3:
			return NF4;
		case 4:
			return NF5;
		case 5:
			return NF6;
		}
		throw new IllegalStateException(
                "this NFtype is non-existent! " + index);
	}


}
