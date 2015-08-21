

import acm.util.*;
import java.io.*;
import java.util.*;

public class Opponent {
	private Map <String, Elite> challengers = new HashMap<String,Elite> ();
	
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
	
	public Elite findEntry(String name) {
		if (challengers.containsKey(name)){
			return challengers.get(name);
		} else {
			return null;
		}
	}
}
