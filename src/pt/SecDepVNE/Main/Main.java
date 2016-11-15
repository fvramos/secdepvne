package pt.SecDepVNE.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

import pt.SecDepVNE.Common.ResourceGenerator;
import pt.SecDepVNE.Substrate.SubstrateCreator;
import pt.SecDepVNE.Substrate.SubstrateNetwork;
import pt.SecDepVNE.Virtual.RequestsCreator;
import pt.SecDepVNE.Virtual.VirtualNetwork;

/**
 * 
 * @author Luis Ferrolho, fc41914, Faculdade de Ciencias da Universidade de Lisboa
 *
 */
public class Main {

	private static String SECDEPMODFILE = "../glpk/modFiles/SecDep_MIP.mod";
	private static String DVINEMODFILE = "../glpk/modFiles/DViNE_MIP.mod";

	private static int POISSON_MEAN = 25;
	private static int TOTAL_TIME = 50000;

	public static void main(String[] args){

		if(args.length != 4){
			System.out.println("Please, choose <real|random|regular>");
			System.out.println("random <nSNodes> <nRequests> <nClouds>");
			System.exit(0);
		}

		if(args.length == 4 && args[0].equalsIgnoreCase("random"))
			executeExperienceType1(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
	}

	private static void executeExperienceType1(int nSNodes, int nRequests, int nClouds) {

		SubstrateCreator subCreator = new SubstrateCreator();

		File f = new File("../gt-itm/graphs/alt_files/saves/random_sub_s"+nSNodes);
		SubstrateNetwork subNet; 

		if(!f.exists()){
			subNet = subCreator.generateRandomSubstrateNetwork("random_sub_s"+nSNodes, nSNodes, nClouds);
			subNet.printToFile("../gt-itm/graphs/alt_files/saves/random_sub_s"+nSNodes);
		}else{
			System.out.println("Using the existent substrate network!");
			subNet = subCreator.generateSubstrateNetwork("../gt-itm/graphs/alt_files/saves/random_sub_s"+nSNodes);
		}

		SubstrateNetwork subNet2 = new SubstrateNetwork(subNet);
		SubstrateNetwork subNet3 = new SubstrateNetwork(subNet);
		SubstrateNetwork subNet4 = new SubstrateNetwork(subNet);
		SubstrateNetwork subNet5 = new SubstrateNetwork(subNet);
		SubstrateNetwork subNet6 = new SubstrateNetwork(subNet);
		SubstrateNetwork subNet7 = new SubstrateNetwork(subNet);
		SubstrateNetwork subNet8 = new SubstrateNetwork(subNet);

		RequestsCreator reqCreator = new RequestsCreator();
		ArrayList<VirtualNetwork> virtualNets = new ArrayList<>();
		ArrayList<VirtualNetwork> virtualNets2 = new ArrayList<>();
		ArrayList<VirtualNetwork> virtualNets3 = new ArrayList<>();
		ArrayList<VirtualNetwork> virtualNets4 = new ArrayList<>();
		ArrayList<VirtualNetwork> virtualNets5 = new ArrayList<>();
		ArrayList<VirtualNetwork> virtualNets6 = new ArrayList<>();
		ArrayList<VirtualNetwork> virtualNets7 = new ArrayList<>();
		ArrayList<VirtualNetwork> virtualNets8 = new ArrayList<>();

		Comparator<Event> comp = new Comparator<Event>() {

			@Override
			public int compare(Event e1, Event e2) {

				return e1.getTime()-e2.getTime();
			}

		};

		PriorityQueue<Event> queue = new PriorityQueue<Event>(1000, comp);
		PriorityQueue<Event> queue2 = new PriorityQueue<>(1000, comp);
		PriorityQueue<Event> queue3 = new PriorityQueue<>(1000, comp);
		PriorityQueue<Event> queue4 = new PriorityQueue<>(1000, comp);
		PriorityQueue<Event> queue5 = new PriorityQueue<>(1000, comp);
		PriorityQueue<Event> queue6 = new PriorityQueue<>(1000, comp);
		PriorityQueue<Event> queue7 = new PriorityQueue<>(1000, comp);
		PriorityQueue<Event> queue8 = new PriorityQueue<>(1000, comp);

		int countk = 0, k = 0, p = 0, start = 0;
		int arrivalTime;

		ResourceGenerator resGen = new ResourceGenerator();

		for(int i = 0; i < nRequests; i++){
			System.out.println("Generating req"+i);
			reqCreator.generateRandomVirtualNetwork("random_req"+i);
		}

		try {
			Thread.sleep(50);

			//TODO Uncomment if there is no file at ../gt-itm/graphs/alt_files/random
//			Utils.generateAltFiles();

			Thread.sleep(50);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for(int i = 0; i < nRequests; i++){
			VirtualNetwork virNet = reqCreator.createVirtualNetworkNoReqs("../gt-itm/graphs/alt_files/random/random_req"+i+".alt", subNet.getNClouds());
			VirtualNetwork virNet1 = reqCreator.createVirtualNetwork("../gt-itm/graphs/alt_files/random/random_req"+i+".alt", subNet.getNClouds());

			if(countk == k){
				k = 0;
				while(k == 0){
					k = resGen.generateArrivalTime(POISSON_MEAN);
				}

				countk = 0;
				start = (p * TOTAL_TIME * POISSON_MEAN) / nRequests;
				p++; 
			}

			arrivalTime = start + ((countk + 1) * TOTAL_TIME * POISSON_MEAN) / (nRequests * (k + 1));
			countk ++;

			virNet.setArrival(arrivalTime);
			virNet1.setArrival(arrivalTime);
			
			virNet.setNodesCPU(virNet1.getNodesCPU());
			virNet.setEdgesBw(virNet1.getEdgesBw());
			
			virtualNets.add(virNet);
			virtualNets2.add(new VirtualNetwork(virNet));
			virtualNets3.add(new VirtualNetwork(virNet1));
			virtualNets4.add(new VirtualNetwork(virNet1));
			virtualNets5.add(new VirtualNetwork(virNet1));
			virtualNets6.add(new VirtualNetwork(virNet1));
			virtualNets7.add(new VirtualNetwork(virNet1));
			virtualNets8.add(new VirtualNetwork(virNet));
			
			queue.add(new Event(EventType.ARRIVE, arrivalTime, i));
			queue2.add(new Event(EventType.ARRIVE, arrivalTime, i));
			queue3.add(new Event(EventType.ARRIVE, arrivalTime, i));
			queue4.add(new Event(EventType.ARRIVE, arrivalTime, i));
			queue5.add(new Event(EventType.ARRIVE, arrivalTime, i));
			queue6.add(new Event(EventType.ARRIVE, arrivalTime, i));
			queue7.add(new Event(EventType.ARRIVE, arrivalTime, i));
			queue8.add(new Event(EventType.ARRIVE, arrivalTime, i));
		}

		Random rand = new Random();

		// No reqs at all
		ArrayList<Double> nodesSec = new ArrayList<>(), edgesSec = new ArrayList<>(), cloudSecurity = new ArrayList<>();
		HashMap<String, Double> cloudsSec = new HashMap<>();
		
		for(int i = 0; i < subNet.getNumOfNodes(); i++){
			nodesSec.add(1.0);
			cloudsSec.put(subNet.getNode(i),1.0);
		}
		
		for(int i = 0; i < subNet.getNumOfEdges(); i++)
			edgesSec.add(1.0);
		
		for(int i = 0; i < subNet.getNClouds(); i++)
			cloudSecurity.add(1.0);
		
		subNet.setCloudsSecurity(cloudSecurity);
		subNet.setCloudSecSup(cloudsSec);
		subNet.setNodesSecurity(nodesSec);
		subNet.setEdgesSecurity(edgesSec);
		
		Worker secDepNoReqsWorker = new Worker("random_exp1", subNet, virtualNets, queue, SECDEPMODFILE);
		Thread secDepNoReqsThread = new Thread(secDepNoReqsWorker);
		secDepNoReqsThread.start();
		
		// No sec or dep in VNs
		Worker secDepNoReqsButCloudsWorker = new Worker("random_exp2", subNet2, virtualNets2, queue2, SECDEPMODFILE);
		Thread secDepNoReqsButCloudsThread = new Thread(secDepNoReqsButCloudsWorker);
		secDepNoReqsButCloudsThread.start();

		// 10 percent of requests ask for dep
		for(VirtualNetwork vnet: virtualNets3){
			int res = rand.nextInt(100);

			if(res >= 0 && res <= 9)
				vnet.setWantBackup(true);
			else{

				int nodes = vnet.getNumOfNodes();
				ArrayList<Integer> locs = new ArrayList<Integer>();

				for(int i = 0; i < nodes; i++)
					locs.add(0);

				vnet.setBackupLocalization(locs);
			}
		}

		Worker secDep10Worker = new Worker("random_exp3", subNet3, virtualNets3, queue3, SECDEPMODFILE);
		Thread secDep10Thread = new Thread(secDep10Worker);
		secDep10Thread.start();

		// 30 percent of requests ask for dep
		for(VirtualNetwork vnet: virtualNets4){
			int res = rand.nextInt(100);

			if(res >= 0 && res <= 29)
				vnet.setWantBackup(true);
			else{

				int nodes = vnet.getNumOfNodes();
				ArrayList<Integer> locs = new ArrayList<Integer>();

				for(int i = 0; i < nodes; i++)
					locs.add(0);

				vnet.setBackupLocalization(locs);
			}
		}

		Worker secDep30Worker = new Worker("random_exp4", subNet4, virtualNets4, queue4, SECDEPMODFILE);
		Thread secDep30Thread = new Thread(secDep30Worker);
		secDep30Thread.start();

		// 50 percent of requests ask for dep
		for(VirtualNetwork vnet: virtualNets5){
			int res = rand.nextInt(100);

			if(res >= 0 && res <= 49)
				vnet.setWantBackup(true);
			else{

				int nodes = vnet.getNumOfNodes();
				ArrayList<Integer> locs = new ArrayList<Integer>();

				for(int i = 0; i < nodes; i++)
					locs.add(0);

				vnet.setBackupLocalization(locs);
			}

		}

		Worker secDep50Worker = new Worker("random_exp5", subNet5, virtualNets5, queue5, SECDEPMODFILE);
		Thread secDep50Thread = new Thread(secDep50Worker);
		secDep50Thread.start();

		// 100 percent of requests ask for dep
		for(VirtualNetwork vnet: virtualNets6)
			vnet.setWantBackup(true);

		Worker secDepWorker = new Worker("random_exp6", subNet6, virtualNets6, queue6, SECDEPMODFILE);
		Thread secDepThread = new Thread(secDepWorker);
		secDepThread.start();
		
		// 25% chances for each one
		for(VirtualNetwork vnet: virtualNets7){
			int res = rand.nextInt(100);
			cloudSecurity = new ArrayList<>();
			nodesSec = new ArrayList<>();
			edgesSec = new ArrayList<>();
			
			if(res >= 0 && res <= 24){
				vnet.setWantBackup(true);

				for(int i = 0; i < vnet.getNumOfNodes(); i++){
					cloudSecurity.add(1.0);
					nodesSec.add(1.0);
				}
				
				for(int i = 0; i < vnet.getNumOfEdges(); i++)
					edgesSec.add(1.0);

				vnet.setNodesSec(nodesSec);
				vnet.setEdgesSec(edgesSec);
				vnet.setCloudsSec(cloudSecurity);

			}else if(res >= 25 && res <= 49){
				vnet.setWantBackup(false);
				ArrayList<Integer> locs = new ArrayList<Integer>();

				for(int i = 0; i < vnet.getNumOfNodes(); i++)
					locs.add(0);

				vnet.setBackupLocalization(locs);
			}else if(res >= 50 && res <= 74){
				vnet.setWantBackup(false);
				
				ArrayList<Integer> locs = new ArrayList<Integer>();

				for(int i = 0; i < vnet.getNumOfNodes(); i++)
					locs.add(0);

				vnet.setBackupLocalization(locs);
				
				for(int i = 0; i < vnet.getNumOfNodes(); i++){
					cloudSecurity.add(1.0);
					nodesSec.add(1.0);
				}
				
				for(int i = 0; i < vnet.getNumOfEdges(); i++)
					edgesSec.add(1.0);

				vnet.setNodesSec(nodesSec);
				vnet.setEdgesSec(edgesSec);
				vnet.setCloudsSec(cloudSecurity);

			}else{
				vnet.setWantBackup(true);
			}
		}
		
		Worker secDepAllWorker = new Worker("random_exp7", subNet7, virtualNets7, queue7, SECDEPMODFILE);
		Thread secDepAllThread = new Thread(secDepAllWorker);
		secDepAllThread.start();

		// Normal objective function of DViNE
		Worker dvineWorker = new Worker("random_exp1", subNet8, virtualNets8, queue8, DVINEMODFILE);
		Thread dvineThread = new Thread(dvineWorker);
		dvineThread.start();
	}

}
