package com.zj.network;

public enum NFtype {
	NF1(0,6),NF2(1,4),NF3(2,3),NF4(3,6);

	private int _index;

	private float _NFcapacity;

	private NFtype(int _index, int _NFcapacity) {
		this._index = _index;
		this._NFcapacity = _NFcapacity;
	}

	public int getIndex() {
		return _index;
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
		}
		throw new IllegalStateException(
                "this NFtype is non-existent! " + index);
	}


}
