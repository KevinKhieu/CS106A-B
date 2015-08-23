/* Opponent object - there are five opponents that the user faces during his/her fight
 * through the Elite Four. This class serves to define the behavior of those opponents. */

import acm.util.*;
import java.io.*;
import java.util.*;

public class Opponent {
	
	// Maps names of opponents to the actual "Opponent Object", labeled Elites in this program.
	private Map <String, Elite> challengers = new HashMap<String, Elite> ();
	
	// Read in file and the string of challengers.
	public Opponent(String filename2){
		try {
			BufferedReader rd = new BufferedReader(new FileReader(filename2));
			while (true) {
				String line = rd.readLine();
				if (line == null) break;
				Elite entry = new Elite(line);
				challengers.put(entry.getName(), entry);
			}
			rd.close();
			} catch(IOException ex) {
				throw new ErrorException(ex);
			}
	}
	
	// Pulls up the Elite we need.
	public Elite findEntry(String name) {
		if (challengers.containsKey(name)){
			return challengers.get(name);
		} else {
			return null;
		}
	}
}
