package com.zj.main;

import com.zj.data.JsonParser;
import com.zj.data.OPLSolver;
import com.zj.draw.JungDrawer;
import com.zj.draw.JungTransformer;
import com.zj.network.Link;
import com.zj.network.Node;
import com.zj.solution.Solution;
import com.zj.test.Test1;


public class App
{
	static String path = "E:\\huangjingkun\\CPLEX\\data\\minnfv\\result_minnfv.json";
	//static String path = "data\\case\\case-ram.json";
	//static String path = "data\\india\\h-india-180-1.json";
	//static String path = "data\\newyork\\h-new-56-1.json";
	//static String path = "data\\pdh\\h-pdh18-int1.json";

	//static String path = "data\\pdh\\pdh18-int1-t-8-6-4-3.json";
	//static String path = "data\\pdh-cost-1\\pdh18-int1-t-c-15.json";


	public static final int VM_CAPACITY = 4;
	public static final float DEFAULT_DELAYRATE = 10;
	public static final int MAX_DELAY = 5;

	public static void main( String[] args )
    {
    	/*
    	Test1 test1 = new Test1();
    	OPLSolver oplmode = new OPLSolver(test1.getTopology());
    	oplmode.startSolveAndShow();
    	*/
		long startTime = System.currentTimeMillis();    //获取开始时间

		//new JsonParser().loadGAJsonData(path);
		new JsonParser().loadOplJsonData(path);
    	//new JsonParser().loadHeuJsonData(path);
    	//new JsonParser().loadHeuPlusJsonData(path);
    	
    	
		long endTime = System.currentTimeMillis();    //获取结束时间

		System.out.println("\n程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间

    }
}
