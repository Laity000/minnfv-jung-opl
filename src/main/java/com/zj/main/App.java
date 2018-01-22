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
	//static String path = "C:\\Users\\PC\\Desktop\\result_minnfv.json";
	//static String path = "C:\\Users\\PC\\Desktop\\data\\case\\case2-6-5-4.json";
	//static String path = "C:\\Users\\PC\\opl\\minlink\\result_minlink.json";
	//static String path = "C:\\Users\\PC\\Desktop\\data\\uncut\\ram93-d-2-70.json";
	//static String path = "C:\\Users\\PC\\Desktop\\data\\pdh\\new\\pdh24-int-d-2-un.json";
	//static String path = "C:\\Users\\PC\\Desktop\\data\\pdh\\new4\\pdh18-int-t-27-6-4-3.json";
	static String path = "C:\\Users\\PC\\Desktop\\data\\pdh\\new4\\h-pdh18-int.json";
	
	public static void main( String[] args )
    {
    	/*
    	Test1 test1 = new Test1();
    	OPLSolver oplmode = new OPLSolver(test1.getTopology());
    	oplmode.startSolveAndShow();
    	*/
		long startTime = System.currentTimeMillis();    //获取开始时间

		//new JsonParser().loadOplJsonData(path);
    	new JsonParser().loadHeuJsonData(path);

		long endTime = System.currentTimeMillis();    //获取结束时间

		System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间

    	

    }
}