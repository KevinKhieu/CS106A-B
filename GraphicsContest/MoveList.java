/* File: MoveList.java
 * Author: Kevin Khieu
 * Defines the list of moves corresponding to each Pokemon by reading in the Pokemon-Moves.txt file */
 
import acm.util.*;
import java.io.*;
import java.util.*;

public class MoveList {
	private Map <String, Move> moveLog = new HashMap<String,Move> ();
	
	public MoveList(String filename2){
		try {
			BufferedReader rd = new BufferedReader(new FileReader(filename2));
			while (true) {
				String line = rd.readLine();
				if (line == null) break;
				Move entry = new Move(line);
				moveLog.put(entry.getName(), entry);
			}
			rd.close();
			} catch(IOException ex) {
				throw new ErrorException(ex);
			}
	
	}
	
	// Getter function for finding Pokemon.
	public Move findEntry(String name) {
		if (moveLog.containsKey(name)){
			return moveLog.get(name);
		} else {
			return null;
		}
	}
}
