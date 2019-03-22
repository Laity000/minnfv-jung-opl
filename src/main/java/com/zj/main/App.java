package com.zj.main;

import com.zj.data.JsonParser;
import com.zj.test.Test1;

public class App
{
	//static String path = "E:\\cplex\\data\\maxflow\\result_minnfv.json";
	//static String path = "data\\case\\case-ram.json";
	//static String path = "data\\india\\h-india-180-1.json";
	//static String path = "data\\newyork\\h-new-56-1.json";
	static String path = "data\\maxthroughput\\pdh\\3\\pdh-80-14-643.json";
	//static String path = "data\\maxthroughput\\pdh\\3\\h-pdh80.json";

	//static String path = "data\\pdh\\pdh18-int1-t-8-6-4-3.json";
	//static String path = "data\\pdh-cost-1\\pdh18-int1-t-c-15.json";

	public static final int VM_CAPACITY = 14;
	public static final float DEFAULT_DELAYRATE = 10;
	public static final int MAX_DELAY = 6;

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
		//new JsonParser().loadThroughputJsonData(path);
		
		//new MutationTest(path).getResult();
		
		long endTime = System.currentTimeMillis();    //获取结束时间

		System.out.println("\n程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
    }
}
