package pt.SecDepVNE.Substrate;

import java.util.ArrayList;

import pt.SecDepVNE.Common.Pair;
import pt.SecDepVNE.Virtual.VirtualNetwork;

/**
 * Manager of substrate resources
 * @author Luis Ferrolho, fc41914, Faculdade de Ciencias da Universidade de Lisboa
 *
 */
public class SubstrateManager {

	private SubstrateNetwork subNet;

	public SubstrateManager(SubstrateNetwork subNet) {
		this.subNet = subNet;
	}

	/**
	 * Updates substrate resources as virtual networks arrive to the system
	 * or depart from it
	 * @param virNet Virtual Network
	 * @param mappedEdges Virtual edges embedded
	 * @param edgesUsed Substrate edges used
	 * @param mappedNodes Virtual nodes embedded
	 * @param nodesUsed Substrate nodes used
	 * @param mode Arrival or departure
	 */
	public void updateSubstrateNetwork(VirtualNetwork virNet, ArrayList<Pair<String>> mappedEdges, ArrayList<Pair<String>> edgesUsed, 
			ArrayList<String> mappedNodes, ArrayList<String> nodesUsed, UpdateMode mode) {

		updateSubstrateEdges(virNet, mappedEdges, edgesUsed, mode);
		updateSubstrateNodes(virNet, mappedNodes, nodesUsed, mode);

	}

	// Update the substrate egdes residual capacity
	private void updateSubstrateEdges(VirtualNetwork virNet, ArrayList<Pair<String>> mappedEdges,
			ArrayList<Pair<String>> edgesUsed, UpdateMode mode) {

		Pair<String> tmpEdge, tmpEdge2;
		double bw = 0;
		ArrayList<Pair<String>> vEdges = virNet.getEdges();

		for(int i = 0; i < edgesUsed.size(); i++){

			tmpEdge = mappedEdges.get(i);
			tmpEdge2 = new Pair<String>(tmpEdge.getRight(), tmpEdge.getLeft());

			if(vEdges.contains(tmpEdge))
				bw = virNet.getEdgeBw(vEdges.indexOf(tmpEdge));
			else if(vEdges.contains(tmpEdge2))
				bw = virNet.getEdgeBw(vEdges.indexOf(tmpEdge2));

			bw = mode == UpdateMode.DECREMENT ? -bw : bw;

			tmpEdge = edgesUsed.get(i);
			tmpEdge2 = new Pair<String>(tmpEdge.getRight(), tmpEdge.getLeft());

			if(subNet.getEdges().contains(tmpEdge))
				subNet.updateEdgeBw(subNet.getEdges().indexOf(tmpEdge), bw);
			else if(subNet.getEdges().contains(tmpEdge2))
				subNet.updateEdgeBw(subNet.getEdges().indexOf(tmpEdge2), bw);

		}
	}

	// Update the substrate nodes residual capacity
	private void updateSubstrateNodes(VirtualNetwork virNet, ArrayList<String> mappedNodes,
			ArrayList<String> nodesUsed, UpdateMode mode) {

		String tmpNode;
		double cpu = 0;
		ArrayList<String> vNodes = virNet.getNodes();

		for(int i = 0; i < nodesUsed.size(); i++){

			tmpNode = mappedNodes.get(i);

			if(vNodes.contains(tmpNode))
				cpu = virNet.getNodeCPU(vNodes.indexOf(tmpNode));

			cpu = mode == UpdateMode.DECREMENT ? -cpu : cpu;
					
			subNet.updateNodeCPU(subNet.getNodes().indexOf(nodesUsed.get(i)), cpu);

		}
	}

}