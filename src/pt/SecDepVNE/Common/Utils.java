package pt.SecDepVNE.Common;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Util funtions needed by the simulator
 * @author Luis Ferrolho, fc41914, Faculdade de Ciencias da Universidade de Lisboa
 *
 */
public class Utils {

	public static String convertToAlphabet(String number) {
		int n = Integer.parseInt(number);
		int mod = 0, tmp = n;

		String res = "";

		if(tmp == 0)
			return "A";

		while (tmp != 0) {
			mod = tmp % 26;
			res = ((char) (65 + mod)) + res;
			tmp /= 26;
		}

		return res;
	}

	public static int convertFromAlphabet(String word) {
		int result = 0, power = 0, mantissa = 0;

		for (int i = word.length() - 1; i >= 0; i--) {
			mantissa = (int)word.charAt(i) - 65;
			result += mantissa * Math.pow(26, power++);
		}

		return result;
	}

	//TODO Uncomment if there is no file at ../gt-itm/graphs/alt_files/random
	/*
	public static void generateAltFiles() {
		try {
			Process p = Runtime.getRuntime().exec("../gt-itm/Runall.sh");
			p.waitFor();
		}catch (InterruptedException e) {
			e.printStackTrace();    
		} catch (IOException e) {
			e.printStackTrace();
		}

	}*/
	
	public static double roundDecimals(double d) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
	    DecimalFormat twoDForm = new DecimalFormat("##.####", otherSymbols);
	    
	    return Double.valueOf(twoDForm.format(d));
	}


	/**
	 * Run the formulation over an input file
	 * @param datFile The input file
	 * @param modFile The formulation
	 * @param outputFile The output file with the results
	 * @return True if it finished before timeout, false otherwise
	 */
	public static boolean runGLPSOL(String datFile, String modFile, String outputFile) {		

		int TIMEOUT = 600; //1800;
		
		try {
			ProcessBuilder builder = new ProcessBuilder("glpsol","--model", modFile, "--data", datFile);
			builder.redirectOutput(new File(outputFile));

			Process p = builder.start();

			// Establish a timer to not allow the mip to run more than TIMEOUT
			long now = System.currentTimeMillis(); 
			long timeoutInMillis = 1000L * TIMEOUT; 
			long finish = now + timeoutInMillis; 

			while(isAlive(p)){
				
				Thread.sleep(10);
				
				if (System.currentTimeMillis() > finish){
					System.out.println("!!! Timeout while solving this request !!!");
					p.destroy();
					return false;
				}
			}
			
		}catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	// Stops the mip execution if there is a timeout
	private static boolean isAlive(Process p) {  
		try{  
			p.exitValue();  
			return false;  
		}catch (IllegalThreadStateException e) {  
			return true;  
		}  
	}
}
