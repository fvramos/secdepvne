package pt.SecDepVNE.Glpk;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import pt.SecDepVNE.Common.Pair;
import pt.SecDepVNE.Virtual.VirtualNetwork;

/**
 * Handles the interpretation of the output files that result from
 * the execution of the formulations
 * @author Luis Ferrolho, fc41914, Faculdade de Ciencias da Universidade de Lisboa
 *
 */
public class OutputFileReader {

	private double backupResources;
	private String executionTime;
	private boolean wasAccepted;

	private ArrayList<Pair<String>> wEdgesUsed;
	private ArrayList<Pair<String>> wMappedEdges;
	
	private ArrayList<Pair<String>> bEdgesUsed;
	private ArrayList<Pair<String>> bMappedEdges;

	private ArrayList<String> wNodesUsed;
	private ArrayList<String> wMappedNodes;
	
	private ArrayList<String> bNodesUsed;
	private ArrayList<String> bMappedNodes;
	
	////////////////////////////////////////////////////////////
	
	private ArrayList<Pair<String>> edgesUsed;
	private ArrayList<Pair<String>> mappedEdges;
	
	private ArrayList<String> nodesUsed;
	private ArrayList<String> mappedNodes;

	public OutputFileReader() {
		this.wEdgesUsed = new ArrayList<>();
		this.wNodesUsed = new ArrayList<>();

		this.wMappedEdges = new ArrayList<>();
		this.wMappedNodes = new ArrayList<>();
		
		this.bNodesUsed = new ArrayList<>();
		this.bMappedNodes = new ArrayList<>();
		
		this.bEdgesUsed = new ArrayList<>();
		this.bMappedEdges = new ArrayList<>();

		this.wasAccepted = true;
		
		////////////////////////////////////////////////////////
		
		edgesUsed = new ArrayList<>();
		mappedEdges = new ArrayList<>();
		
		nodesUsed = new ArrayList<>();
		mappedNodes = new ArrayList<>();
	}

	/**
	 * Interprets the results of the execution of the formulations
	 * @param virNet Virtual network that tried the embedding
	 * @param numOfNodes Number of substrate nodes in the substrate network
	 * @param outputFile File that has to be interpreted
	 */
	public void collectAllInfo(VirtualNetwork virNet, int numOfNodes, String outputFile) {

		cleanAllInfo();

		String line = null;
		String[] parts = null, parts2 = null;

		Pair<String> tmp = null;
		
		try {
			FileReader fileReader = new FileReader(outputFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while( (line = bufferedReader.readLine()) != null && !line.isEmpty() && wasAccepted) {
				parts = line.split(" +");

				// Don't need lines with one word or less
				if(parts.length < 2)
					continue;

				parts2 = parts[1].split("\\[|\\]|,");
				
				// Check if the embedding was successful
				if(line.equals("PROBLEM HAS NO PRIMAL FEASIBLE SOLUTION") || 
				   line.equals("PROBLEM HAS NO INTEGER FEASIBLE SOLUTION") || 
				   line.equals("LP HAS NO PRIMAL FEASIBLE SOLUTION"))
					wasAccepted = false;

				// Read time execution and the quantity of resources used for backup
				if(parts[0].equalsIgnoreCase("time") && parts[1].contains("used"))
					executionTime = parts[2];
				else if(parts[1].contains("r[") && !parts[2].equalsIgnoreCase("0.000000"))
					backupResources += Double.parseDouble(parts[2]);
				else if(parts[1].contains("gama[") && !parts[2].equalsIgnoreCase("0.000000"))
					backupResources += Double.parseDouble(parts[2]);

				if((parts[1].contains("fw[") && !parts[2].equalsIgnoreCase("0.000000")) || 
						(parts[1].contains("fb[") && !parts[2].equalsIgnoreCase("0.000000"))){ //BW

					if(outputFile.contains("SecDep")){ //SECDEP
						
						if(parts[1].contains("fw[")){
							tmp = new Pair<>(parts2[1],parts2[2]);
							wMappedEdges.add(tmp);

							tmp = new Pair<>(parts2[3],parts2[4]);
							wEdgesUsed.add(tmp);
						}else if(parts[1].contains("fb[")){
							tmp = new Pair<>(parts2[1],parts2[2]);
							bMappedEdges.add(tmp);

							tmp = new Pair<>(parts2[3],parts2[4]);
							bEdgesUsed.add(tmp);
						}
						
						tmp = new Pair<>(parts2[1],parts2[2]);
						mappedEdges.add(tmp);

						tmp = new Pair<>(parts2[3],parts2[4]);
						edgesUsed.add(tmp);
						
					}else{ //DVINE
						parts = parts2[1].split("f");
					
						tmp = virNet.getEdge(Integer.parseInt(parts[1]));
					
						String n1 = String.valueOf(Integer.parseInt(tmp.getLeft()) + numOfNodes);
						String n2 = String.valueOf(Integer.parseInt(tmp.getRight()) + numOfNodes);
						
						if(!parts2[2].equals(n1) && !parts2[2].equals(n2) && !parts2[3].equals(n1) && !parts2[3].equals(n2)){
							wMappedEdges.add(tmp);
							mappedEdges.add(tmp);
							
							tmp = new Pair<String>(parts2[2],parts2[3]);
							wEdgesUsed.add(tmp);
							edgesUsed.add(tmp);
						}

					}

				}else if((parts[1].contains("thetaw[") && !parts[2].equalsIgnoreCase("0")) || 
						(parts[1].contains("thetab[") && !parts[2].equalsIgnoreCase("0"))){ //CPU

					if(outputFile.contains("SecDep")){ //SECDEP
						
						if(parts[1].contains("thetaw[")){
							wMappedNodes.add(parts2[1]);
							wNodesUsed.add(parts2[2]);
						}else if(parts[1].contains("thetab[")){
							bMappedNodes.add(parts2[1]);
							bNodesUsed.add(parts2[2]);
						}
						
						mappedNodes.add(parts2[1]);
						nodesUsed.add(parts2[2]);
						
					}else{ //DVINE

						String opLeft = String.valueOf(Math.abs(numOfNodes - Integer.parseInt(parts2[1])));
						String opRight = String.valueOf(Math.abs(numOfNodes - Integer.parseInt(parts2[2])));
						
						if(virNet.getNodes().contains(opLeft) && !virNet.getNodes().contains(opRight) && Integer.parseInt(parts2[1]) >= numOfNodes){
							wMappedNodes.add(opLeft);
							wNodesUsed.add(parts2[2]);
							mappedNodes.add(opLeft);
							nodesUsed.add(parts2[2]);
						}

					}
				}
			}

			bufferedReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Empty all attributes
	 */
	public void cleanAllInfo() {
		wasAccepted = true;
		wEdgesUsed.clear();
		wMappedEdges.clear();
		bEdgesUsed.clear();
		bMappedEdges.clear();
		wNodesUsed.clear();
		wMappedNodes.clear();
		bNodesUsed.clear();
		bMappedNodes.clear();
		backupResources = 0;
		mappedEdges.clear();
		edgesUsed.clear();
		mappedNodes.clear();
		nodesUsed.clear();
	}

	public double getBackupResourcesQuantity() {
		return backupResources;
	}

	public String getExecutionTime() {
		return executionTime;
	}

	public boolean wasAccepted() {
		return wasAccepted;
	}
	
	public void setWasAccepted(boolean wasAccepted) {
		this.wasAccepted = wasAccepted;
	}

	public ArrayList<Pair<String>> getwEdgesUsed() {
		return wEdgesUsed;
	}

	public ArrayList<Pair<String>> getwMappedEdges() {
		return wMappedEdges;
	}
	
	public ArrayList<Pair<String>> getEdgesUsed() {
		return edgesUsed;
	}

	public ArrayList<Pair<String>> getMappedEdges() {
		return mappedEdges;
	}
	
	public ArrayList<Pair<String>> getbEdgesUsed() {
		return bEdgesUsed;
	}

	public ArrayList<Pair<String>> getbMappedEdges() {
		return bMappedEdges;
	}

	public ArrayList<String> getwNodesUsed() {
		return wNodesUsed;
	}

	public ArrayList<String> getwMappedNodes() {
		return wMappedNodes;
	}
	
	public ArrayList<String> getNodesUsed() {
		return nodesUsed;
	}

	public ArrayList<String> getMappedNodes() {
		return mappedNodes;
	}
	
	public ArrayList<String> getbNodesUsed() {
		return bNodesUsed;
	}

	public ArrayList<String> getbMappedNodes() {
		return bMappedNodes;
	}

}
