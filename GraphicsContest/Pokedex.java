/* A Pokedex serves as this program's mapping of each pokemon to it's corresponding 
 * Pokemon object, which matches the moves and stats of pokemon in battle to their correct counterpart.
 * One Pokedex object per read in file (so whatever file is read in must have the entire pokemon gropu in it)
 */

import acm.util.*;
import java.io.*;
import java.util.*;

public class Pokedex {
	
	// HashMap of Names of Pokemon to Pokemon objects
	private Map <String, Pokemon>database = new HashMap<String,Pokemon> ();
	
	// Upon initializing a Pokedex, we read in a file name line by line. 
	// Each line is a Pokemon with stats.
	public Pokedex (String filename1) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader(filename1));
			while (true) {
				String line = rd.readLine();
				if (line == null) break;
				Pokemon entry = new Pokemon(line);
				database.put(entry.getName(), entry);
			}
			rd.close();
			} catch(IOException ex) {
				throw new ErrorException(ex);
			}
	}
	
	// Grabber function for finding Pokemon in Pokedex
	public Pokemon findEntry(String name) {
		if (database.containsKey(name)){
			return database.get(name);
		} else {
			return null;
		}
	}
}
