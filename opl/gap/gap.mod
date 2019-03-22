/*********************************************
 * OPL 12.7.1.0 Model
 * Author: zj
 * Creation Date: 2019年3月11日 at 下午3:02:22
 *********************************************/
 
float temp;
execute{
	var before = new Date();
	temp = before.getTime();
}

 
 
 
int NumPMs = ...;   // Number of nodes
range PMs = 1..NumPMs;

// Get the set of NFs
{string} NFs = ...;
// Provide the number of VM of NF in a node: numVM = total resource / VM's resource in a NF (CPU、memory)

// Provide the number of throughput in VM of NF
int VMtroughput[NFs] = ...;
// PM capacity
int PMcapacity = ...;
////////////////////////

///////////demands/////////////
// Number of demands

//Create a record to hold information about each flow demand
tuple demand {
	int id;
	int srcnode;
	int destnode;
	int supply;
	int NFC[NFs];
	int profil[PMs];
}
// Get the set of demands
{demand} Demands = ...;
/////////////////////////


////////////decision variables/////////////////
// The network flow model has decision binary variables indexed on the demands and the arcs. 

//demand is admitted
dvar boolean X[Demands][NFs][PMs];

dvar boolean Z[Demands];                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
//
dvar int+ TotalFunc[PMs][NFs];  

////////////////////////////////////////////////


dexpr int TotalThrought =  sum (i in Demands) Z[i] * i.supply;
dexpr int TotalDelay = sum(i in Demands, k in NFs, j in PMs) X[i][k][j] * i.profil[j];
maximize TotalThrought - 0.001 * TotalDelay;

subject to {
	//sfc constraints
  
	  
	forall (i in Demands)
	  sfcB:
	  sum (k in NFs, j in PMs) X[i][k][j] == sum (k in NFs : i.NFC[k] > 0) Z[i];
	  
	//vnf constraints
	forall (i in Demands, k in NFs : i.NFC[k] > 0)
	  vnfA:
	  sum (j in PMs) X[i][k][j] == Z[i]; 
	
	forall (i in Demands, k in NFs : i.NFC[k] == 0)
	  vnfB:
	  sum (j in PMs) X[i][k][j] == 0; 
	  
	  
	//resouces constraints
	forall (j in PMs, k in NFs)
	  resoucesA:
	  sum (i in Demands) X[i][k][j] * i.supply / VMtroughput[k] <= TotalFunc[j][k];
	  
	forall (j in PMs)
	  resoucesB:
	  sum (k in NFs) TotalFunc[j][k] <= PMcapacity;
	  
	
	  
	  
	
	      
}


 ///////////////////////////////////////////////////////////// 
   execute PRINT {  
  
   var after = new Date();
 
   var ofile = new IloOplOutputFile("result_minnfv.json"); 
   
   //BestObject
   ofile.write("//BestObject\": ",  cplex.getBestObjValue(), "\n");
   
   //Time
   ofile.write("//Time\": ",  after.getTime()-temp, "\n");
   
   
   //acceptedflowid
   ofile.write("\n \"acceptedflowid\": [");
   for (var i in Demands){
		if (Z[i] == 1)
   		   ofile.write(i.id, ", ");
   }   		
   ofile.write("],");
   
   //acceptedflowpm
   var pmsite = new Array(); 
   pmsite[0]=1;       
   pmsite[1]=5;
   pmsite[2]=7;
   pmsite[3]=8;
   ofile.write("\n \"acceptedflowpm\": [");
   for (var i in Demands){
		if (Z[i] == 1){
   		   ofile.write("\n  {", i.id, "(",i.srcnode,",",i.destnode,"): ");
   		   for (var k in NFs){
   		   	  for (var j in PMs){
   		   	     if (X[i][k][j] == 1){
   		   	        ofile.write(k, "->", pmsite[j-1], "  ");
                 }             
              }   		   	    
           }
           ofile.write("}");   	
        }           
           	   
   		   	     
   }   		
   ofile.write("\n]");
    
   
         
   ofile.close();

} 