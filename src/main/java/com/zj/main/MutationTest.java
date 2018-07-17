package com.zj.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.zj.data.JsonParser;
import com.zj.solution.Solution;


@SuppressWarnings("unchecked")
public class MutationTest {
	

	// 创建多个有返回值的任务
	private List<Future> list = new ArrayList<Future>(11);

	String path;

	public MutationTest(String path) {	
		this.path = path;

		// 创建一个线程池
        ExecutorService pool = Executors.newFixedThreadPool(11);
		// 创建多个有返回值的任务
        for (int i = 0; i < 11; i++) {
            list.add(pool.submit(new SoluUnit()));
        }
        // 关闭线程池
        pool.shutdown();
	}
		

	public double getResult(){
		//11次结果(适应度函数值)
		double[] result = new double[11];
		// 获取所有并发任务的运行结果
        for (int i = 0; i < list.size(); i++) {
			try {
				result[i] = (double) list.get(i).get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        //取中间值
        Arrays.sort(result);
    	System.out.println("中间值：" + result[5]);
    	return result[5];
	}


	private class SoluUnit implements Callable<Double>{
		
		@Override
		public Double call() throws Exception {
			Solution solu = new JsonParser().loadGAJsonData(path);
			return solu.getFitValue();
		}

	}
	
		

}