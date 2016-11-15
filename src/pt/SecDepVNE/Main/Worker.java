package pt.SecDepVNE.Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PriorityQueue;

import pt.SecDepVNE.Common.Pair;
import pt.SecDepVNE.Common.Utils;
import pt.SecDepVNE.Glpk.DVineDatFileCreator;
import pt.SecDepVNE.Glpk.OutputFileReader;
import pt.SecDepVNE.Glpk.SecDepDatFileCreator;
import pt.SecDepVNE.Substrate.SubstrateManager;
import pt.SecDepVNE.Substrate.SubstrateNetwork;
import pt.SecDepVNE.Substrate.UpdateMode;
import pt.SecDepVNE.Virtual.VirtualNetwork;

/**
 * 
 * @author Luis Ferrolho, fc41914, Faculdade de Ciencias da Universidade de Lisboa
 *
 */
public class Worker implements Runnable{

	private SubstrateNetwork subNet;
	private ArrayList<VirtualNetwork> virNets;
	private String modFile;
	private String graphstype;

	private PriorityQueue<Event> queue;

	public Worker(String graphstype, SubstrateNetwork subNet, ArrayList<VirtualNetwork> virNets, PriorityQueue<Event> queue, String modFile) {
		this.subNet = subNet;
		this.virNets = virNets;
		this.modFile = modFile;
		this.graphstype = graphstype;
		this.queue = queue;
	}

	@Override
	public void run() {

		ArrayList<OutputFileReader> fileReaders = new ArrayList<>();
		SubstrateManager subMngr = new SubstrateManager(subNet);
		int i = 0;

		if(modFile.contains("SecDep_MIP")){

			try {
				FileWriter fileWriter = new FileWriter("../statistics/SecDep/"+graphstype+"/output_s"+subNet.getNumOfNodes()+".txt");
				BufferedWriter writer = new BufferedWriter(fileWriter);

				writer.write("EventIndex \t Time \t Duration \t EventType \t Accepted \t TotalRev \t TotalCost \t AvgNodeUtilization \t AvgLinkUtilization \t MIPExecTime\n");

				SecDepDatFileCreator secDepDatCreator = new SecDepDatFileCreator();

				while(!queue.isEmpty()){

					Event curEvent = queue.peek();
					i = curEvent.getIndex();
					
					//System.out.println("curEvent "+i);

					fileReaders.add(new OutputFileReader());

					if(curEvent.getEventType() == EventType.ARRIVE){

						secDepDatCreator.createDatFile("../glpk/datFiles/SecDep/"+graphstype+"/random_req"+i+".dat", subNet, virNets.get(i));

						//System.out.println("[SecDep_"+graphstype+"] Request"+i+" arrived!");
						boolean res = Utils.runGLPSOL("../glpk/datFiles/SecDep/"+graphstype+"/random_req"+i+".dat", modFile, 
								"../glpk/outputFiles/SecDep/"+graphstype+"/random_req"+i+".txt");

						if(res){
							fileReaders.get(i).collectAllInfo(virNets.get(i), subNet.getNumOfNodes(),
									"../glpk/outputFiles/SecDep/"+graphstype+"/random_req"+i+".txt");

							if(fileReaders.get(i).wasAccepted()){
								subMngr.updateSubstrateNetwork(virNets.get(i), fileReaders.get(i).getMappedEdges(), fileReaders.get(i).getEdgesUsed(), 
										fileReaders.get(i).getMappedNodes(), fileReaders.get(i).getNodesUsed(), UpdateMode.DECREMENT);

								queue.add(new Event(EventType.DEPART, virNets.get(i).getArrival() + virNets.get(i).getDuration(), i));
							}

						}else{
							fileReaders.get(i).setWasAccepted(res);
						}

					}else if(curEvent.getEventType() == EventType.DEPART){
						subMngr.updateSubstrateNetwork(virNets.get(i), fileReaders.get(i).getMappedEdges(), fileReaders.get(i).getEdgesUsed(), 
								fileReaders.get(i).getMappedNodes(), fileReaders.get(i).getNodesUsed(), UpdateMode.INCREMENT);
					}

					double totalRev = virNets.get(i).getRevenue();
					double totalCost = subNet.getSecDepCost(virNets.get(i), fileReaders.get(i).getwNodesUsed(), fileReaders.get(i).getwMappedNodes(),
							fileReaders.get(i).getbNodesUsed(), fileReaders.get(i).getbMappedNodes(), fileReaders.get(i).getwEdgesUsed(), fileReaders.get(i).getwMappedEdges(),
							fileReaders.get(i).getbEdgesUsed(), fileReaders.get(i).getbMappedEdges());
					
					double avgNU = subNet.getAverageNodeStress();
					double avgLU = subNet.getAverageLinkStress();

					writer.write(i+"  "+curEvent.getTime()+"  "+virNets.get(i).getDuration()+"  "+(curEvent.getEventType() == EventType.ARRIVE ? "ARRIVE" : "DEPART"));

					writer.write("  "+fileReaders.get(i).wasAccepted()+"  "+String.format(Locale.ENGLISH,"%10.4f",totalRev)+"  "+String.format(Locale.ENGLISH,"%10.4f",totalCost)+"  "+String.format(Locale.ENGLISH,"%10.4f",avgNU)+"  "+String.format(Locale.ENGLISH,"%10.4f",avgLU)+"  "+fileReaders.get(i).getExecutionTime()+"\n");

					//System.out.println("[SecDep_"+graphstype+"] Removing: "+queue.poll().getIndex());
					
					queue.poll().getIndex();
				}

				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//Same thing as above but for DViNE_MIP
		if(modFile.contains("DViNE_MIP")){
			
			String directory = modFile.contains("ModifiedDViNE_MIP.mod") ? "ModifiedDViNE" : "DViNE";
			
			try{
				
				FileWriter fileWriter = new FileWriter("../statistics/"+directory+"/"+graphstype+"/output_s"+subNet.getNumOfNodes()+".txt");
				BufferedWriter writer = new BufferedWriter(fileWriter);

				writer.write("EventIndex \t Time \t Duration \t EventType \t Accepted \t TotalRev \t TotalCost \t AvgNodeUtilization \t AvgLinkUtilization \t MIPExecTime\n");

				DVineDatFileCreator dVineDatCreator = new DVineDatFileCreator();

				ArrayList<ArrayList<String>> tmp = new ArrayList<>();
				ArrayList<ArrayList<Pair<String>>> tmp2 = new ArrayList<>();

				while(!queue.isEmpty()){

					Event curEvent = queue.peek();
					i = curEvent.getIndex();
					
					//System.out.println("Request "+i);

					fileReaders.add(new OutputFileReader());
					tmp.add(new ArrayList<String>());
					tmp2.add(new ArrayList<Pair<String>>());

					if(curEvent.getEventType() == EventType.ARRIVE){

						dVineDatCreator.createDatFile("../glpk/datFiles/"+directory+"/"+graphstype+"/random_req"+i+".dat", subNet, virNets.get(i));

						//System.out.println("["+directory+"] Request"+i+" arrived!");
						boolean res = Utils.runGLPSOL("../glpk/datFiles/"+directory+"/"+graphstype+"/random_req"+i+".dat", modFile, 
								"../glpk/outputFiles/"+directory+"/"+graphstype+"/random_req"+i+".txt");

						if(res){

							fileReaders.get(i).collectAllInfo(virNets.get(i), subNet.getNumOfNodes(), 
									"../glpk/outputFiles/"+directory+"/"+graphstype+"/random_req"+i+".txt");
							
							if(fileReaders.get(i).wasAccepted()){
								for(String s: fileReaders.get(i).getNodesUsed()){
									tmp.get(i).add(Utils.convertToAlphabet(s));
								}

								for(Pair<String> s: fileReaders.get(i).getEdgesUsed()){
									tmp2.get(i).add(new Pair<String>(Utils.convertToAlphabet(s.getLeft()), Utils.convertToAlphabet(s.getRight())));
								}

								subMngr.updateSubstrateNetwork(virNets.get(i), fileReaders.get(i).getMappedEdges(), tmp2.get(i), 
										fileReaders.get(i).getMappedNodes(), tmp.get(i), UpdateMode.DECREMENT);

								queue.add(new Event(EventType.DEPART, virNets.get(i).getArrival() + virNets.get(i).getDuration(), i));
							}

						}else
							fileReaders.get(i).setWasAccepted(res);
					}else if(curEvent.getEventType() == EventType.DEPART){
						subMngr.updateSubstrateNetwork(virNets.get(i), fileReaders.get(i).getMappedEdges(), tmp2.get(i), 
								fileReaders.get(i).getMappedNodes(), tmp.get(i), UpdateMode.INCREMENT);
					}
					
					double totalRev = virNets.get(i).getRevenue();
					double totalCost = subNet.getDvineCost(virNets.get(i), tmp2.get(i), fileReaders.get(i).getMappedEdges());

					double avgNU = subNet.getAverageNodeStress();
					double avgLU = subNet.getAverageLinkStress();

					writer.write(i+"  "+curEvent.getTime()+"  "+virNets.get(i).getDuration()+"  "+(curEvent.getEventType() == EventType.ARRIVE ? "ARRIVE" : "DEPART"));

					writer.write("  "+fileReaders.get(i).wasAccepted()+"  "+String.format(Locale.ENGLISH,"%10.4f",totalRev)+"  "+String.format(Locale.ENGLISH,"%10.4f",totalCost)+"  "+String.format(Locale.ENGLISH,"%10.4f",avgNU)+"  "+String.format(Locale.ENGLISH,"%10.4f",avgLU)+"  "+fileReaders.get(i).getExecutionTime()+"\n");

					//System.out.println("["+directory+"] Removing: "+queue.poll().getIndex());
					
					queue.poll().getIndex();

				}

				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
