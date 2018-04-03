/*********************************************
 * OPL 12.7.1.0 Model
 * Author: PC
 * Creation Date: 2018年1月22日 at 下午4:09:09
 *********************************************/

 
int NumNodes = ...;   // Number of nodes
range Nodes = 0..NumNodes-1;

/////////////arcs////////
// Create a record to hold information about each arc
tuple arc {
   key int fromnode;
   key int tonode;
   float capacity;
}
// Get the set of arcs
{arc} Arcs = ...;//集合
//////////////////////////

////////NFs///////////
// Number of NFs
int NumNFs = ...;  
range NFnum = 1..NumNFs;
// Get the set of NFs
{string} NFs = ...;
// Provide the number of VM of NF in a node: numVM = total resource / VM's resource in a NF (CPU、memory)
int NumVM = ...;
// Provide the number of throughput in VM of NF
int VMtroughput[NFs] = ...;
////////////////////////

///////////demands/////////////
// Number of demands
int NumDemands = ...; 
range Dems = 1..NumDemands;
//Create a record to hold information about each flow demand
tuple demand {
	int id;
	int srcnode;
	int destnode;
	float supply;
	int splen;
	int NFC[NFs];
}
// Get the set of demands
{demand} Demands = ...;
//
float MaxDelayRate = ...;
/////////////////////////


////////////decision variables/////////////////
// The network flow model has decision binary variables indexed on the demands and the arcs. 
dvar boolean Flow[Dems][Arcs];

//
dvar boolean Func[Dems][Nodes][NFs];
//
dvar int+ TotalFunc[Nodes][NFs];  
//
dvar boolean ActivedNode[Nodes];                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
//
dvar int+ FlowOrdinal[Dems][Nodes];

dvar int+ TempOrdinal[Dems][Nodes][NFs];

dvar int+ FuncOrdinal[Dems][NFs];

////////////////////////////////////////////////


dexpr int TotalFuncMun =  sum (i in Nodes, m in NFs) TotalFunc[i][m];
dexpr int TotalAcitviedNodeMun =  sum (i in Nodes) ActivedNode[i];
//minimize TotalAcitviedNodeMun  + MaxDelay/1000;
minimize 100 * TotalAcitviedNodeMun + TotalFuncMun ;
subject to {
	
	 // flow balance constraints
   	forall (k in Dems, i in Nodes)
     ctNodeFlow:
      sum (<i,j,c> in Arcs) Flow[k][<i,j,c>]
    - sum (<j,i,c> in Arcs) Flow[k][<j,i,c>] 
    == sum (dem in Demands : dem.id == k && dem.srcnode == i) 1
    - sum (dem in Demands : dem.id == k && dem.destnode == i) 1;
	
	// bandwidth constraints
	/*
	forall (a in Arcs)
	  ctBandwidth:
	    sum(dem in Demands) Flow[dem.id][a]*dem.supply
	  + sum(dem in Demands, <i,j,c> in Arcs : i == a.tonode && j == a.fromnode)Flow[dem.id][<i,j,c>]*dem.supply
	  <= a.capacity;
	  */
	 
	// node's VM num constraints and actived nodes constraints
	forall (i in Nodes)
	  ctNodeResourse:
	    sum (m in NFs) TotalFunc[i][m] <= ActivedNode[i] * NumVM;
	   
	// VM throughput constraints: if overflow, VM +1
	forall (i in Nodes, m in NFs)
	  ctNFsum:
	  	sum (dem in Demands) Func[dem.id][i][m] * dem.supply / VMtroughput[m] <= TotalFunc[i][m];
	  	
//	// actived nodes constraints
//	forall (i in Nodes, m in NFs)
//	  ctActivedNodes:
//	  	TotalFunc[i][m] / NumVM <= ActivedNode[i];
	 
	///////////flowordinal////////
	// circuit constraints and flowordinal upper constraints
	forall(k in Dems, <i,j,c> in Arcs) 
	 stCircuit:
	    FlowOrdinal[k][j] >= FlowOrdinal[k][i] + Flow[k][<i,j,c>] - NumNodes* (1- Flow[k][<i,j,c>]);
	
	// flowordinal lower constraints, non-existent flowordinal is 0
	forall(k in Dems, i in Nodes)
	  ctFlowOrdinalLower:
	    FlowOrdinal[k][i] <= sum (<i,j,c> in Arcs) NumNodes*Flow[k][<i,j,c>] + sum (<j,i,c> in Arcs) NumNodes*Flow[k][<j,i,c>];
	
	// flowordinal origin constraints, origin node is NO.1
	
	forall(dem in Demands, i in Nodes : dem.srcnode == i)
	  ctFlowOrdinalOrigin:
	   FlowOrdinal[dem.id][i] == 1;
	//////////////////////////////
	
	// delay constraints
	forall (dem in Demands)
	  ctdelay:
	  	 sum (a in Arcs) Flow[dem.id][a]  <= MaxDelayRate * dem.splen;  	 
	     
	     
	// NFC num lower constraints: non-existent func is 0
	forall (dem in Demands, m in NFs)
	  ctNFCnum:
	    sum (i in Nodes) Func[dem.id][i][m] <= dem.NFC[m];
	 
	// NFC num upper constraints
	forall (dem in Demands, m in NFs : dem.NFC[m] > 0)
	  ctNFCUpperNum:
	    sum (i in Nodes) Func[dem.id][i][m] == 1;
	    
		
	// NFC site constraints: func must be in flows
	forall (k in Dems, i in Nodes, m in NFs)
	  ctNFCsite:
	    sum (<i,j,c> in Arcs)  Flow[k][<i,j,c>] + sum (<j,i,c> in Arcs)  Flow[k][<j,i,c>]
	    >= Func[k][i][m]; 
	  
	 
	// NFC funcordinal definition: funcordinal is flowordinal, except for non-existent func  
	// ==> FuncOrdinal[k][m] == Func[k][i][m] * FlowOrdinal[k][i];
	//////////////// 
	forall (k in Dems, i in Nodes, m in NFs)
	  ctA:
	    TempOrdinal[k][i][m] >= FlowOrdinal[k][i] - NumNodes * (1 - Func[k][i][m]);
	forall (k in Dems, i in Nodes, m in NFs)
	  ctB:
	   TempOrdinal[k][i][m] <= NumNodes * Func[k][i][m];
	forall (k in Dems, i in Nodes, m in NFs)
	  ctC:
	   TempOrdinal[k][i][m]<= FlowOrdinal[k][i];
	forall (k in Dems, m in NFs)
	  ctD:
	   FuncOrdinal[k][m] == sum (i in Nodes) TempOrdinal[k][i][m];
	//////////  
	
	// NFC funcordinal order constraints
	forall (dem in Demands, f in NFnum)
	  forall(m in NFs,n in NFs : dem.NFC[m] == f && dem.NFC[n] == f+1)
	    ctFuncOrdianl:
	      FuncOrdinal[dem.id][n] >= FuncOrdinal[dem.id][m];
	      
}


execute DISPLAY {



   writeln("\nFlow<id,fromnode,tonode,Flow[k][a]>\n");
   for(var k in Dems)
   	for(var a in Arcs)
   	   if(Flow[k][a] > 0)
         writeln("<",k,",",a.fromnode,",",a.tonode,",",Flow[k][a],">");
         
   writeln("\nFlowOrdinal<demand,nodes,ordinal>\n");      
   for(var k in Dems)
   	for(var i in Nodes)
   	   if(FlowOrdinal[k][i] > 0)
         writeln("<",k,",",i,",",FlowOrdinal[k][i],">");
        
   write("\nTempOrdinal<demand,node,NF,ordinal>:\n");
   for(var k in Dems)
      for(var i in Nodes)
    	 for(var m in NFs)
    	 	if(TempOrdinal[k][i][m] > 0)
    	 	   write("<",k,",",i,",",m,",",TempOrdinal[k][i][m],">\n");
   /* 
    write("\nFuncOrdinal<demand,NF,ordinal>:\n");
   for(var k in Dems)
    	 for(var m in NFs)
    	 	if(FuncOrdinal[k][m] >=0)
    	 	   write("<",k,",",m,",",FuncOrdinal[k][m],">\n");
    */
    /*     
   write("\nFunc<demand,node,NF,num>:\n");
   for(var k in Dems)
      for(var i in Nodes)
    	 for(var m in NFs)
    	 	if(Func[k][i][m] >0)
    	 	   write("<",k,",",i,",",m,",",Func[k][i][m],">\n");
    */	 	   
   write("\nTotalFunc<node,NF,num>\n");
   for (var i in Nodes)
     for(var m in NFs)
       if (TotalFunc[i][m] > 0)
        write("<",i,",",m,",",TotalFunc[i][m],">\n");
   
   write("\nActivedNode<node,num>\n");
   for (var i in Nodes)
     if (ActivedNode[i] > 0)
       write("<",i,",",ActivedNode[i],">\n");
  
   write("\nMaxDelay:",MaxDelay,"\n");  
}

 ///////////////////////////////////////////////////////////// 
   execute PRINT {  
  
 
   var ofile = new IloOplOutputFile("result_minnfv.json"); 
   
   //BestObject
   ofile.write("//BestObject\": ",  cplex.getBestObjValue(), "\n");
   
   //NumNodes
   ofile.write("{\n \"numNodes\": ", NumNodes , ",");
   
   //Arcs
   ofile.write("\n \"arcs\": [");
   var count = 0;
   for (var a in Arcs){
		if(count++ !=0){
			ofile.write(",");	
		}  
   		ofile.write("\n  {\n   \"fromNodeID\": ", a.fromnode, ",\n   \"toNodeID\": ", a.tonode, 
   		",\n   \"capacity\": ", a.capacity, "\n  }");
   }   		
   ofile.write("\n ],");
   
   //Demands
   ofile.write("\n \"demands\": [");
   count = 0;
   for (var d in Demands){
      if(count++ !=0){
			ofile.write(",");	
		}  
		ofile.write("\n  {\n   \"id\": ", d.id, ",\n   \"srcNodeID\": ", d.srcnode,
		",\n   \"destNodeID\": ", d.destnode, ",\n   \"supply\": ", d.supply, ",\n   \"SFC\": [");
		var count1 = 0;
		for (var m in NFs){
			if(count1++ != 0){
				ofile.write(", ");			
			}		
			ofile.write(d.NFC[m]);		
		}
		ofile.write("]\n  }")
   }
   ofile.write("\n ],");
    
   
   //Flow
   ofile.write("\n \"flows\": [");
   count = 0; 
   for(var k in Dems){
   	for(var a in Arcs){
   	   if(Flow[k][a] > 0){
   	   	  if(count++ !=0){
			ofile.write(",");	
		} 
        ofile.write("\n  {\n   \"demandID\": ", k, ",\n   \"fromNodeID\": ", a.fromnode, 
         ",\n   \"toNodeID\": ", a.tonode, "\n  }");
       }
     } 
   }                    
   ofile.write("\n ],");
   
   //TempOrdinal
   ofile.write("\n \"tempOrdinals\": [");
   count = 0;
    for(var k in Dems){
      for(var i in Nodes){
    	  for(var m in NFs){
    	 	if(TempOrdinal[k][i][m] > 0){
    	 	  if(count++ != 0){
			    ofile.write(",");	     	 	
    	 	  }
    	 	  ofile.write("\n  {\n   \"demandID\": ", k, ",\n   \"nodeID\": ", i, 
    	 	   ",\n   \"NFString\": \"", m, "\",\n   \"ordinal\": ", TempOrdinal[k][i][m], "\n  }");  
			}
 		 }			
      }
  } 		
   ofile.write("\n ]");      
   
   ofile.write("\n}"); 
         
   ofile.close();

} 