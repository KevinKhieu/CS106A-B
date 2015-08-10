/* File: FacePamphletDatabase.java
 * -------------------------------
 * This class keeps track of the profiles of all users in the
 * FacePamphlet application. 
 */

import java.util.*;
import java.io.*;

public class FacePamphletDatabase implements FacePamphletConstants {

	// FacePamphletDatabase works as a Hashmap that reads in a profile name and pairs it with its corresponding profile.
	private Map<String, FacePamphletProfile> database = new HashMap<String,FacePamphletProfile>();
	
	// Constructor for Database object
	public FacePamphletDatabase() {
	  
	}
	
	/*  This method adds the given profile to the database.  If the name associated with the profile 
	 *  is the same as an existing name in the database, the existing profile is replaced.
	 */
	public void addProfile(FacePamphletProfile profile) {
		if (database.containsKey(profile.getName())){
			// If we do have one, we remove the old profile and replace it with the new profile.
			database.remove(profile.getName());
			database.put(profile.getName(), profile);
		} else {
			// If the database doesn't have the passed profile, we add the profile to the database.
			database.put(profile.getName(), profile);
		}
	}
		
	/* This method returns the profile associated with the given name 
	 * in the database.  If there is no profile, returns null.
	 */
	public FacePamphletProfile getProfile(String name) {
		/* Adjust the entry of the friend being called upon to match the format of names in our database.
		 * Ex.: "kevin KHIEU" -> "Kevin Khieu" */
		String friend = adjustEntry(name);
		
		// If we have the profile, we return that profile. If not, we return nothing.
		if (database.containsKey(friend)){
			FacePamphletProfile returnProfile = database.get(friend);
			return returnProfile;
		}
		return null;
	}
	
	
	/* This method removes the profile associated with the given name
	 * from the database.  It also updates the list of friends of all
	 * other profiles in the database to make sure that this name is
	 * removed from the list of friends of any other profile.
	 * 
	 * If there is no profile in the database with the given name, then
	 * the database is unchanged after calling this method.
	 */
	public void deleteProfile(String name) {
		String friend = adjustEntry(name);
		
		/* If we have the a profile on hand with the given name, we remove it from our database and all friendlists
		 * who have this name as a friend. */
		if (database.containsKey(friend)){
			FacePamphletProfile removedProfile = database.get(friend);
			Iterator<String> iterator = removedProfile.getFriends();
			while (iterator.hasNext()){
				String profile = iterator.next();
				FacePamphletProfile fpfriend = database.get(profile);
				fpfriend.removeFriend(friend);
			}
			database.remove(friend);
		}
	}

	
	/** 
	 * This method returns true if there is a profile in the database 
	 * that has the given name.  It returns false otherwise.
	 */
	public boolean containsProfile(String name) {
		String friend = adjustEntry(name);
		if (database.containsKey(friend))
			return true;
			return false;
	}
	
	/* Adjusts each entry that is read so that they are the same format.
	 / Ex: "kevIn khieu" and "KeViN kHIEu" end up as "Kevin Khieu" */ 
	private String adjustEntry(String friend){
		StringTokenizer tokenizer = new StringTokenizer(friend);
		String finalName = "";
		while (tokenizer.hasMoreTokens()){
			// Makes each Token in the tokenizer a new word with only the first letter of the word uppercase.
			// Ex.: "kevin" -> "Kevin"
			String next = tokenizer.nextToken();
			char first = next.charAt(0);
			String rest = next.substring(1);
			first = Character.toUpperCase(first);
			rest = rest.toLowerCase();
			String edittedName = first + rest;
			        
			// Adds the fixed name to the finalName string.
			finalName += edittedName + " ";
		}
		// Fix finalName so that it doesn't have that awkward space at the end.
		finalName = finalName.substring(0, finalName.length()-1);
    		return finalName;
	}
}
