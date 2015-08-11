/*
 * File: FacePamphletProfile.java
 * ------------------------------
 * This class keeps track of all the information for one profile
 * in the FacePamphlet social network.  Each profile contains a
 * name, an image (which may not always be set), a status (what 
 * the person is currently doing, which may not always be set),
 * and a list of friends.
 */

import acm.graphics.*;
import java.util.*;
import java.io.*;

public class FacePamphletProfile implements FacePamphletConstants {
	
	private String userName = "";
	private GImage userImage = null;
	private String userStatus;
	private ArrayList <String> friendList= new ArrayList<String>();
	
	/** 
	 * Constructor
	 * This method takes care of any initialization needed for
	 * the profile.
	 */
	public FacePamphletProfile(String name) {
		String friend = adjustEntry(name);
		
		// Set userName to the given name.
		// Set the user's status to the default String.
		userName = friend;
		userStatus = "No current status.";
	}

	/** This method returns the name associated with the profile. */ 
	public String getName() {
		return userName;
	}

	/** 
	 * This method returns the image associated with the profile.  
	 * If there is no image associated with the profile, the method
	 * returns null. */ 
	public GImage getImage() {
		// If the image is blank, we return null. Otherwise we return the image.
		if (userImage == null){
			return null;
		}
		return userImage;
	}

	/** This method sets the image associated with the profile. */ 
	public void setImage(GImage image) {
		userImage = image;
	}
	
	/** 
	 * This method returns the status associated with the profile.
	 * If there is no status associated with the profile, the method
	 * returns the empty string ("").
	 */ 
	public String getStatus() {
		return userStatus;
	}
	
	/** This method sets the status associated with the profile. */ 
	public void setStatus(String status) {
		userStatus = status;
	}

	/** 
	 * This method adds the named friend to this profile's list of 
	 * friends.  It returns true if the friend's name was not already
	 * in the list of friends for this profile (and the name is added 
	 * to the list).  The method returns false if the given friend name
	 * was already in the list of friends for this profile (in which 
	 * case, the given friend name is not added to the list of friends 
	 * a second time.)
	 */
	public boolean addFriend(String friend) {
        String name = adjustEntry(friend);
		if (friendList.contains(name)){
			return false;
		} else {
			// Adds the name of the friend to this friendList if it does not already have him/her as a friend.
			friendList.add(name);
			return true;
		}
	}

	/** 
	 * This method removes the named friend from this profile's list
	 * of friends.  It returns true if the friend's name was in the 
	 * list of friends for this profile (and the name was removed from
	 * the list).  The method returns false if the given friend name 
	 * was not in the list of friends for this profile (in which case,
	 * the given friend name could not be removed.)
	 */
	public boolean removeFriend(String friend) {
		// Adjust the entry we are given.
		String name = adjustEntry(friend);   
		
		// Removes the friend from this friend list of it contains the name.
		if (friendList.contains(name)){
			friendList.remove(friendList.indexOf(name));
			return true;
		
			//Or else we do nothing and return false.
		} else{
			return false;
		}
	}

	/** 
	 * This method returns an iterator over the list of friends 
	 * associated with the profile.
	 */ 
	public Iterator<String> getFriends() {	
		return friendList.iterator();
	}
	
	/** 
	 * This method returns a string representation of the profile.  
	 * This string is of the form: "name (status): list of friends", 
	 * where name and status are set accordingly and the list of 
	 * friends is a comma separated list of the names of all of the 
	 * friends in this profile.
	 * 
	 * For example, in a profile with name "Alice" whose status is 
	 * "coding" and who has friends Don, Chelsea, and Bob, this method 
	 * would return the string: "Alice (coding): Don, Chelsea, Bob"
	 */ 
	public String toString() {
		String facePamphletString = userName + " (" + userStatus + "): ";
		Iterator<String> friendIterator = friendList.iterator();
		while (friendIterator.hasNext()){
			facePamphletString = facePamphletString + friendIterator.next() + ", ";
		}
		return facePamphletString;
	}
	
	/* Adjusts each entry that is read so that they are the same format.
	 / Ex: "kevIn khieu" and "KeViN kHIEu" end up as "Kevin Khieu" */ 
	private String adjustEntry(String friend){
		StringTokenizer tokenizer = new StringTokenizer(friend);
		
		// Set an empty string to hold our updated name. 
		String finalName = "";
		while (tokenizer.hasMoreTokens()){
			// Makes each Token in the tokenizer a new word with only the first letter of the word uppercase.
			String next = tokenizer.nextToken();
			char first = next.charAt(0);
			first = Character.toUpperCase(first);
			String rest = next.substring(1);
			rest = rest.toLowerCase();
			String edittedName = first + rest;
			
			// Adds the fixed name to the finalName string.
			finalName += edittedName + " ";
		}
		// Fix finalName so that it doesn't have that awkward space at the end.
		finalName = finalName.substring(0,finalName.length()-1);
        	return finalName;
	}
}
