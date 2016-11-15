package pt.SecDepVNE.Charts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import pt.SecDepVNE.Main.EventType;

/**
 * Class responsible for the creation of the .dat files of all experiments
 * @author Luis Ferrolho, fc41914, Faculdade de Ciencias da Universidade de Lisboa
 *
 */
public class DatCreator {

	public static void main(String[] args) {

		int nNodes = Integer.parseInt(args[0]); // Number of substrate nodes

		// Input files of each experiment
		String inputSecDepFileExp1 = "../statistics/SecDep/random_exp1/output_s"+nNodes+".txt";
		String inputSecDepFileExp2 = "../statistics/SecDep/random_exp2/output_s"+nNodes+".txt";
		String inputSecDepFileExp3 = "../statistics/SecDep/random_exp3/output_s"+nNodes+".txt";
		String inputSecDepFileExp4 = "../statistics/SecDep/random_exp4/output_s"+nNodes+".txt";
		String inputSecDepFileExp5 = "../statistics/SecDep/random_exp5/output_s"+nNodes+".txt";
		String inputSecDepFileExp6 = "../statistics/SecDep/random_exp6/output_s"+nNodes+".txt";
		String inputSecDepFileExp7 = "../statistics/SecDep/random_exp7/output_s"+nNodes+".txt";

		String inputDvineFile = "../statistics/DViNE/random_exp1/output_s"+nNodes+".txt";
		
		// Output file to each experiment
		String outputSecDepFileExp1 = "../plots/SecDep_exp1.dat";
		String outputSecDepFileExp2 = "../plots/SecDep_exp2.dat";
		String outputSecDepFileExp3 = "../plots/SecDep_exp3.dat";
		String outputSecDepFileExp4 = "../plots/SecDep_exp4.dat";
		String outputSecDepFileExp5 = "../plots/SecDep_exp5.dat";
		String outputSecDepFileExp6 = "../plots/SecDep_exp6.dat";
		String outputSecDepFileExp7 = "../plots/SecDep_exp7.dat";

		String outputDvineFile = "../plots/DViNE.dat";
		
		// Construct all .dat files
		makeAll(inputSecDepFileExp1, outputSecDepFileExp1);
		makeAll(inputSecDepFileExp2, outputSecDepFileExp2);
		makeAll(inputSecDepFileExp3, outputSecDepFileExp3);
		makeAll(inputSecDepFileExp4, outputSecDepFileExp4);
		makeAll(inputSecDepFileExp5, outputSecDepFileExp5);
		makeAll(inputSecDepFileExp6, outputSecDepFileExp6);
		makeAll(inputSecDepFileExp7, outputSecDepFileExp7);

		makeAll(inputDvineFile, outputDvineFile);

	}

	/**
	 * Creates a .dat file to generate the plots after 
	 * @param inputFile Read a file with all the info of an experiment
	 * @param outputFile Write a file with the info structured to plot 
	 */
	public static void makeAll(String inputFile, String outputFile) {
		String[] parts;
		String line;
		
		int time = 0;
		EventType event; // Arrival or depart
		boolean accepted; // Accepted or not
		double rev = 0, cost = 0, avgNU = 0, avgLU = 0; // revenue, cost, nodeUtilization, linkUtilization
		int acCount = 0, total_ac = 0; // Total accepted
		int index = 0;

		double tRev = 0, tCost = 0, tAvgNode = 0, tAvgLink = 0;  

		try {
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader reader = new BufferedReader(fileReader);

			FileWriter fileWriter = new FileWriter(outputFile);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			
			reader.readLine(); //Read first line
			
			while( (line = reader.readLine()) != null){

				parts = line.split(" +");
			
				index = Integer.parseInt(parts[0]);
				time = Integer.parseInt(parts[1]);
				event = parts[3].contains("ARRIVE") ? EventType.ARRIVE : EventType.DEPART;
				accepted = parts[4].contains("true") ? true: false;
				rev = Double.parseDouble(parts[5]);
				cost = Double.parseDouble(parts[6]);
				avgNU = Double.parseDouble(parts[7]);
				avgLU = Double.parseDouble(parts[8]);
				
				if(event == EventType.ARRIVE){
					if(accepted){
						
						acCount += 1;
						total_ac++;
						
						tRev += rev;
						tCost += cost;
						tAvgNode += avgNU;
						tAvgLink += avgLU;
						
						// Write the info
						writer.write(time+" "+String.format(Locale.ENGLISH,"%10.4f",tRev/(double)time)+" "+String.format(Locale.ENGLISH,"%10.4f",tCost/(double)acCount)+
								" "+String.format(Locale.ENGLISH,"%10.4f",tAvgNode/(double)acCount)+" "+String.format(Locale.ENGLISH,"%10.4f",tAvgLink/(double)acCount)+
								" "+String.format(Locale.ENGLISH,"%10.4f",(double)total_ac/(double)(index+1))+"\n");
						
					}
				}
			}
			
			reader.close();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
