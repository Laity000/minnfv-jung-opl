package com.zj.data;

import ilog.concert.IloException;
import ilog.concert.IloIntMap;
import ilog.concert.IloSymbolSet;
import ilog.concert.IloTuple;
import ilog.concert.IloTupleSet;
import ilog.cplex.IloCplex;
import ilog.opl.IloOplDataSource;
import ilog.opl.IloOplErrorHandler;
import ilog.opl.IloOplException;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModel;
import ilog.opl.IloOplModelDefinition;
import ilog.opl.IloOplModelSource;
import ilog.opl.IloOplSettings;
import java.util.Collection;
import java.util.LinkedList;
import com.zj.draw.JungDrawer;
import com.zj.draw.JungTransformer;
import com.zj.network.Link;
import com.zj.network.NFtype;
import com.zj.network.Node;
import com.zj.network.Topology;
import com.zj.solution.Flow;
import com.zj.solution.Solution;

/**
 *
 * @Title: OPLSolver.java
 * @Description: TODO (已弃用)直接调用cplex-opl运行得到对象数据
 * @author ZhangJing
 *
 */
public class OPLSolver {
	// static final String DATADIR = ".";
	static final String DATADIR = "C:\\Users\\PC\\opl\\minlink";
	static final String MODFILENAME = "/minlink.mod";
	static final String DATFILENAME = "/minlink.dat";
	/*
	 * cplex
	 */
	private IloCplex cplex;
	/*
	 * opl
	 */
	private IloOplModel opl;
	/*
	 * 拓扑信息
	 */
	private Topology topology;
	/*
	 * 解决方案，即流的路径集
	 */

	public OPLSolver(Topology topology) {
		this.topology = topology;
	}

	public void startSolveAndShow() {
		int status = 127;
		try {
			IloOplFactory.setDebugMode(true);
			// Create one OPL factory by thread.
			// The OPL factory will create and handle all the needed OPL
			// objects.
			IloOplFactory oplF = new IloOplFactory();
			// Create an error handler.
			IloOplErrorHandler errHandler = oplF.createOplErrorHandler();

			// Create the OPL model source based on the .mod file.
			IloOplModelSource modelSource = oplF.createOplModelSource(DATADIR + MODFILENAME);
			// Create the OPL data source based on the .dat file.
			IloOplDataSource dataSource = oplF.createOplDataSource(DATADIR + DATFILENAME);

			// Create the default settings.
			IloOplSettings settings = oplF.createOplSettings(errHandler);
			// Create the OPL model definition by linking the source and the
			// settings.
			IloOplModelDefinition def = oplF.createOplModelDefinition(modelSource, settings);
			// Gets the algorithm.
			cplex = oplF.createCplex();

			// Create the OPL model from the OPL definition and the algorithm.
			opl = oplF.createOplModel(def, cplex);
			// Add the the different data sources to the OPL model.
			opl.addDataSource(dataSource);
			// Generate the model.
			opl.generate();
			cplex.setOut(null);

			if (cplex.solve()) {
				System.out.println("OBJECTIVE: " + opl.getCplex().getObjValue());
				opl.postProcess();
				// opl.printSolution(System.out);
				Solution solution = printSolution();

				/*
				for(Flow flow : solution.getFlows()){
					System.out.println(flow);
				}
				*/
				showGraph(solution);

			} else {
				System.err.println("No solution!");
			}
			oplF.end();
			status = 0;
		} catch (IloOplException ex) {
			System.err.println("### OPL exception: " + ex.getMessage());
			ex.printStackTrace();
			status = 2;
		} catch (IloException ex) {
			System.err.println("### CONCERT exception: " + ex.getMessage());
			ex.printStackTrace();
			status = 3;
		} catch (Exception ex) {
			System.err.println("### UNEXPECTED UNKNOWN ERROR ...");
			ex.printStackTrace();
			status = 4;
		}
		// System.exit(status);
		// System.out.println(solution.getFlows().size());

	}

	private Solution printSolution() {
		String[] splitstr = DATADIR.split("\\\\");
		String soluname = splitstr[splitstr.length - 1];
		// System.out.println("Solution " + soluname);
		Solution solution = new Solution(100);
		printFlow(solution);
		//printNFC(solution);
		return solution;

	}

	private void printFlow(Solution solution) {
		try {
			IloIntMap oplFlow = opl.getElement("Flow").asIntMap();
			int oplDemNum = opl.getElement("NumDemands").asInt();
			// int arcsNum = opl.getElement("Arcs").asTupleSet().getSize();
			IloTupleSet arcs = opl.getElement("Arcs").asTupleSet();
			for (int i = 1; i <= oplDemNum; i++) {
				Collection<Link> paths = new LinkedList<Link>();
				for (java.util.Iterator it = arcs.iterator(); it.hasNext();) {
					IloTuple t = (IloTuple) it.next();
					if (oplFlow.getSub(i).get(t) == 1) {
						// System.out.println(i + "," +
						// t.getIntValue("fromnode") + "," +
						// t.getIntValue("tonode"));
						int fromid = t.getIntValue("fromnode");
						int toid = t.getIntValue("tonode");
						Link path = topology.getLink(fromid, toid);
						// 检查流的路径是否与拓扑的链路反向
						if (path == null) {
							path = topology.getLink(toid, fromid);
							if (path == null) {
								throw new IllegalStateException(
										"this flow is non-existent in topology." + fromid + "/ toid" + toid);
							}
						}
						/*
						if (paths.contains(path)) {
							throw new IllegalStateException(
									"this flow already contains a path with fromid " + fromid + "/ toid" + toid);
						}
						*/
						paths.add(path);
					}
				}
				solution.addFlow(i, paths);
			}
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("opl solution \"flow\" error.: " + e.getMessage());
		}
	}

	private void printNFC(Solution solution) {
		try {
			IloIntMap oplTempOrdinal = opl.getElement("TempOrdinal").asIntMap();
			int oplDemNum = opl.getElement("NumDemands").asInt();
			int oplNodeNum = opl.getElement("NumNodes").asInt();
			IloSymbolSet oplNFs = opl.getElement("NFs").asSymbolSet();
			for (int i = 1; i <= oplDemNum; i++) {
				for (int j = 0; j < oplNodeNum; j++) {
					for (java.util.Iterator it = oplNFs.iterator(); it.hasNext();) {
						String NFname = (String) it.next();

						int NFordinal = oplTempOrdinal.getSub(i).getSub(j).get(NFname);
						if(NFordinal > 0){
							//System.out.print(" ordinal" + ordinal);
							solution.addFlow(i, NFtype.valueOf(NFname), topology.getNode(j), NFordinal);
							// TODO: 处理VM
						}
					}
				}
			}

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("opl solution \"NFC\" error.: " + e.getMessage());
		}

	}

	private void showGraph(Solution solution) {
		JungDrawer<Node, Link> jd = new JungDrawer<Node, Link>(JungTransformer.setTopoTransformer(topology),
				JungTransformer.setSoluTransformer(solution));
		jd.initDraw();
	}

}
